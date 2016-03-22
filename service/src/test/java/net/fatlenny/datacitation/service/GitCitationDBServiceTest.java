package net.fatlenny.datacitation.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.fatlenny.datacitation.api.Query;

public class GitCitationDBServiceTest {
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private File projectFolder;

    private GitCitationDBService service;

    @Before
    public void setUp() throws IOException {
        File file = new File("src/test/resources/gitCitationDB");

        temporaryFolder.create();
        projectFolder = temporaryFolder.newFolder();

        FileUtils.copyDirectory(file, projectFolder);

        service = new GitCitationDBService(projectFolder.getAbsolutePath());
    }

    @Test
    public void getDatabaseTables_shouldReturnSingleTable() {
        List<String> databaseTableNames = service.getDatasetNames();
        assertTrue(databaseTableNames.size() == 1);
        assertTrue(databaseTableNames.get(0).equals("ZAMG-MetroData"));
    }

    @Test
    public void getQueries_shouldReturnSingleTable() {
        List<Query> queries = service.getQueries();
        assertTrue(queries.size() == 1);
        
        Query query = queries.get(0);
        // time=1457223257
        assertEquals(1457223257L, query.getTime());
        assertEquals("SELECT \"Datum\",\"Name\" FROM ZAMG-MetroData WHERE Datum='02-01-2016' AND Name='Retz'",
                     query.getQuery());
        assertEquals("ZAMG-MetroData.csv", query.getDatasetName());
        assertEquals("cd8544c", query.getCommit().getRevisionId());
    }

}
