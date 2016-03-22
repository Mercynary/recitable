/**
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.fatlenny.datacitation.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import net.fatlenny.datacitation.api.CitationDBException;
import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.api.PID;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.api.Revision;
import net.fatlenny.datacitation.api.Status;
import net.fatlenny.datacitation.api.TableModel;
import net.fatlenny.datacitation.api.TableModelMetaData;

public class GitCitationDBService implements CitationDBService {
    private static final String QUERY_ENDING = "query";
    private static final String CSV_ENDING = "csv";

    private static final String GIT_FOLDER = ".git";
    private static final String REF_MASTER = "refs/heads/master";
    private static final String REF_QUERIES = "refs/heads/queries";

    private static final String COMMIT_KEY = "commit";
    private static final String DATASET_KEY = "dataset";
    private static final String DESCRIPTION_KEY = "description";
    private static final String PID_KEY = "pid";
    private static final String QUERY_KEY = "query";
    private static final String TIME_KEY = "time";


    private static Logger LOG = LoggerFactory.getLogger(GitCitationDBService.class);

    private Repository repository;
    private String databaseLocation;

    @Inject
    public GitCitationDBService(@Named("databaseLocation") String databaseLocation) {
        this.databaseLocation = databaseLocation;
        prepareGitRepository();
        createMasterBranchIfNotExists();
        createQueriesBranchIfNotExists();
    }

    @Override
    public List<String> getDatasetNames() throws CitationDBException {
        checkoutBranch(REF_MASTER);

        List<String> databaseFileNames = new ArrayList<>();
        String workingTreeDir = getWorkingTreeDir();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(workingTreeDir), "*." + CSV_ENDING)) {
            for (Path path : ds) {
                String pathName = path.getFileName().toString();
                databaseFileNames.add(pathName.substring(0, pathName.lastIndexOf(".")));
            }
        } catch (IOException e) {
            throw new CitationDBException("Error reading data files: ", e);
        }

        return databaseFileNames;
    }

    @Override
    public List<Query> getQueries() throws CitationDBException {
        checkoutBranch(REF_QUERIES);

        List<Query> queries = new ArrayList<>();
        String workingTreeDir = getWorkingTreeDir();

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(workingTreeDir), "*." + QUERY_ENDING)) {
            for (Path path : ds) {
                queries.add(getQueryFromFile(path));
            }
        } catch (IOException e) {
            throw new CitationDBException("Error reading data files: ", e);
        }

        return queries;
    }

    @Override
    public Query getQueryById(String pid) {
        checkoutBranch(REF_QUERIES);

        String workingTreeDir = getWorkingTreeDir();

        Path path = Paths.get(workingTreeDir, DigestUtils.sha1Hex(pid) + ".query");
        return getQueryFromFile(path);
    }

    @Override
    public TableModel getQueryResult(Query query) {
        ObjectId head = checkoutBranch(query.getCommit().getRevisionId());

        String workingTreeDir = getWorkingTreeDir();

        return retrieveDatasetForQuery(workingTreeDir, query.getQuery(), head);
    }

    @Override
    public TableModel loadDataset(String name) throws CitationDBException {
        ObjectId head = checkoutBranch(REF_MASTER);

        String workingTreeDir = getWorkingTreeDir();
        String query = String.format("SELECT * FROM %s", name);

        return retrieveDatasetForQuery(workingTreeDir, query, head);
    }

    @Override
    public Status saveQuery(Query query) throws CitationDBException {
        checkoutBranch(REF_QUERIES);

        String pidIdentifier = query.getPid().getIdentifier();
        String fileName = DigestUtils.sha1Hex(pidIdentifier) + "." + QUERY_ENDING;
        try (Git git = new Git(repository)) {
            Path filePath = Paths.get(getWorkingTreeDir(), fileName);
            Properties properties = writeQueryToProperties(query);
            properties.store(Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW),
                             "");
            git.add().addFilepattern(fileName).call();
            PersonIdent personIdent = new PersonIdent("ReCitable", "recitable@fatlenny.net");
            String message = String.format("Created query file for PID=%s", pidIdentifier);
            git.commit().setMessage(message).setAuthor(personIdent).setCommitter(personIdent).call();
        } catch (IOException | GitAPIException e) {
            throw new CitationDBException(e);
        }

        return Status.SUCCESS;
    }

    private Properties writeQueryToProperties(Query query) {
        Properties properties = new Properties();
        properties.setProperty(COMMIT_KEY, query.getCommit().getRevisionId());
        properties.setProperty(DATASET_KEY, query.getDatasetName());
        properties.setProperty(DESCRIPTION_KEY, query.getDescription());
        properties.setProperty(PID_KEY, query.getPid().getIdentifier());
        properties.setProperty(QUERY_KEY, query.getQuery());
        properties.setProperty(TIME_KEY, String.valueOf(query.getTime()));
        return properties;
    }

    private ObjectId checkoutBranch(String commit) throws CitationDBException {
        try (Git git = new Git(repository)) {
            git.checkout().setName(commit).call();
            return repository.resolve(Constants.HEAD);
        } catch (GitAPIException e) {
            throw new CitationDBException("Error checking out master branch: ", e);
        } catch (NoWorkTreeException e) {
            throw new CitationDBException("Error retrieving data folder: ", e);
        } catch (IOException e) {
            throw new CitationDBException("Error resolving head revision");
        }
    }

    private void createMasterBranchIfNotExists() throws CitationDBException {
        try (Git git = new Git(repository)) {
            String master = Constants.MASTER;
            ObjectId head = repository.resolve(REF_MASTER);
            if (head == null) {
                git.commit().setMessage("Initial commit").call();
                String readmeFileName = "README.md";
                String[] text = new String[] { "DO NOT DELETE DIRECTORY. USED BY RECITABLE" };
                Files.write(Paths.get(getWorkingTreeDir(), readmeFileName), Arrays.asList(text),
                            StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE);
                git.add().addFilepattern(readmeFileName).call();
                PersonIdent personIdent = new PersonIdent("ReCitable", "recitable@fatlenny.net");
                git.commit().setMessage("Created README.md").setAuthor(personIdent).setCommitter(personIdent).call();
            }
        } catch (RevisionSyntaxException | IOException | GitAPIException e) {
            throw new CitationDBException("Error creating branch master", e);
        }
    }

    private void createQueriesBranchIfNotExists() throws CitationDBException {
        try {
            ObjectId head = repository.resolve(REF_QUERIES);
            if (head == null) {
                new Git(repository).checkout().setName("queries").setOrphan(true).call();
                new Git(repository).rm().addFilepattern(".").call();
                new Git(repository).commit().setMessage("Initial commit").call();
            }
        } catch (RevisionSyntaxException | IOException | GitAPIException e) {
            throw new CitationDBException("Error creating branch queries", e);
        }
    }

    private Connection getCSVConnection(String directory) throws CitationDBException {
        Properties props = new Properties();
        props.put("separator", ";");
        props.put("charset", "UTF-8");

        try {
            Class.forName("org.relique.jdbc.csv.CsvDriver");
            return DriverManager.getConnection("jdbc:relique:csv:" + directory, props);
        } catch (ClassNotFoundException | SQLException e) {
            throw new CitationDBException("Couldn't create database connection to database " + directory, e);
        }
    }

    private Query getQueryFromFile(Path path) throws CitationDBException {
        Properties properties = new Properties();

        try {
            properties.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new CitationDBException("Error loading query file. ", e);
        }

        Revision rev = new DefaultRevision(properties.getProperty(COMMIT_KEY));
        String pidIdentifier = properties.getProperty(PID_KEY);
        PID pid = new DefaultPID.PIDBuilder(pidIdentifier).setName(pidIdentifier).build();

        String queryString = properties.getProperty(QUERY_KEY);
        String datasetString = properties.getProperty(DATASET_KEY);
        long time = Long.parseLong(properties.getProperty(TIME_KEY));
        String description = properties.getProperty(DESCRIPTION_KEY);
        Query query = new DefaultQuery.QueryBuilder(pid, queryString, datasetString, rev).setTime(time)
            .setDescription(description)
            .build();

        return query;
    }

    /**
     * Retrieves the absolute path of the working tree directory.
     * 
     * @return the path of the working tree directory as string.
     */
    private String getWorkingTreeDir() throws CitationDBException {
        return repository.getWorkTree().getAbsolutePath();
    }

    private void prepareGitRepository() {
        File gitFolder = new File(databaseLocation, GIT_FOLDER);
        LOG.debug("Using {} as database location.", databaseLocation);

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            repository = builder.setGitDir(gitFolder).build();
            if (!Files.exists(gitFolder.toPath())) {
                repository.create();
            }
        } catch (IOException e) {
            throw new CitationDBException("Error creating repository", e);
        }
    }

    private TableModel retrieveDatasetForQuery(String directory, String query, ObjectId head)
        throws CitationDBException {

        try (Connection conn = getCSVConnection(directory)) {
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(query);
            ResultSetMetaData metaData = set.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            TableModelMetaData tableMetaData = new DefaultTableModelMetaData(new DefaultRevision(head.getName()));
            DefaultTableModel model = new DefaultTableModel(tableMetaData, Arrays.asList(columns));

            while (set.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = (String) set.getObject(i);
                    
                }
                model.addRow(row);
            }

            return model;
        } catch (SQLException e) {
            throw new CitationDBException(e);
        }
    }
}
