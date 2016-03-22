package net.fatlenny.datacitation.webapp.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.google.inject.Inject;

import net.fatlenny.datacitation.api.CitationDBException;
import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.api.PID;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.api.Revision;
import net.fatlenny.datacitation.api.TableModel;
import net.fatlenny.datacitation.service.DefaultPID;
import net.fatlenny.datacitation.service.DefaultQuery;
import net.fatlenny.datacitation.service.DefaultRevision;
import net.fatlenny.datacitation.webapp.config.Constants;

public class DatasetCreationPage extends WebPage {
	private static final long serialVersionUID = 1L;

    private static final String FEEDBACK = "No %s passed as parameter";

    private String revision;

    @Inject
    private CitationDBService citationDBService;

    public DatasetCreationPage(final PageParameters parameters) {
		super(parameters);

        StringValue datasetParameter = parameters.get(Constants.DATASET_PARAM);
        StringValue queryParameter = parameters.get(Constants.QUERY_PARAM);

        add(new BookmarkablePageLink<Void>("home", HomePage.class));

        add(new FeedbackPanel("feedback"));

        if (datasetParameter.isEmpty()) {
            error("No dataset selected.");
        }

        final Label selectedDataset = new Label("selectedDataset", datasetParameter.toString("No dataset chosen!"));
        add(selectedDataset);

        initializeQueryForm(datasetParameter.toString(), queryParameter.toString(""));
        initializeDatatable(datasetParameter.toString(), queryParameter.toString(""));
    }

    private void initializeQueryForm(String datasetName, String query) {
        final TextArea<String> queryField = new TextArea<String>("queryInput", Model.of(query));

        Form<?> form = new Form<Void>("parameterForm");

        form.add(queryField);

        Button tryButton = new Button("try") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                String queryValue = queryField.getModelObject();
                if (queryValue == null) {
                    queryValue = "";
                }

                PageParameters pageParameters = new PageParameters();
                pageParameters.add(Constants.QUERY_PARAM, queryValue);
                pageParameters.add(Constants.DATASET_PARAM, datasetName);
                getRequestCycle().setResponsePage(DatasetCreationPage.class, pageParameters);
            }
        };

        form.add(tryButton);

        Button saveButton = new Button("save") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                String queryValue = queryField.getModelObject();
                if (queryValue == null) {
                    queryValue = "";
                }

                PageParameters pageParameters = new PageParameters();
                pageParameters.add(Constants.QUERY_PARAM, queryValue);
                pageParameters.add(Constants.DATASET_PARAM, datasetName);
                pageParameters.add(Constants.REV_PARAM, revision);
                getRequestCycle().setResponsePage(DatasetSavePage.class, pageParameters);
            }
        };

        form.add(saveButton);

        add(form);
    }

    private void initializeDatatable(String selectedFile, String queryString) {
        if (selectedFile == null) {
            add(new Label("datatable", "Error populating data table."));
            return;
        }

        try {
            TableModel tableModel;
            if (queryString.isEmpty()) {
                tableModel = citationDBService.loadDataset(selectedFile.toString());
            } else {
                String pidIdentifier = UUID.randomUUID().toString();
                PID pid = new DefaultPID.PIDBuilder(pidIdentifier).setName(pidIdentifier).build();

                Revision revision = new DefaultRevision("HEAD");

                Query query = new DefaultQuery.QueryBuilder(pid, queryString, selectedFile, revision).build();

                tableModel = citationDBService.getQueryResult(query);
            }

            revision = tableModel.getMetaData().getRevision().getRevisionId();

            List<IColumn> header = new ArrayList<>();
            List<String> headerData = tableModel.getHeaderData();

            for (int i = 0; i < headerData.size(); i++) {
                header.add(new PropertyColumn<>(new Model<>(headerData.get(i)), String.format("%s", i)));
            }
            List<String[]> rows = tableModel.getRowData();

            ListDataProvider<String[]> dataProvider = new ListDataProvider<>(rows);

            DataTable<String, String> table = new DataTable("datatable", header, dataProvider, 15);
            table.addBottomToolbar(new NavigationToolbar(table));
            table.addTopToolbar(new HeadersToolbar(table, null));
            add(table);
        } catch (CitationDBException e) {
            error(e.getMessage());
            add(new Label("datatable", "Error populating data table."));
        }
    }
}
