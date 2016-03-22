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
package net.fatlenny.datacitation.webapp.pages;

import java.util.List;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.webapp.config.Constants;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(HomePage.class);

    private String selectedDataset;
    private Query selectedQuery;

    @Inject
    private CitationDBService citationService;

	public HomePage(final PageParameters parameters) {
		super(parameters);

        add(new BookmarkablePageLink<Void>("home", HomePage.class));

        initializeDatasetSelection();
        initializeQuerySelection();
    }

    private void initializeDatasetSelection() {
        boolean databaseFormEnabled = false;
        List<String> databaseFiles = citationService.getDatasetNames();
        LOG.debug("{} database files retrieved", databaseFiles.size());

        if (!databaseFiles.isEmpty()) {
            databaseFormEnabled = true;
            selectedDataset = databaseFiles.get(0);
        }

        add(new FeedbackPanel("feedback"));

        DropDownChoice<String> dbFiles = new DropDownChoice<String>(
                "datasets", new PropertyModel<String>(this, "selectedDataset"), databaseFiles);

        Form<?> databaseForm = new Form<Void>("datasetForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(Constants.DATASET_PARAM, selectedDataset);
                setResponsePage(DatasetCreationPage.class, pageParameters);

            }
        };

        add(databaseForm);
        databaseForm.add(dbFiles);
        databaseForm.setEnabled(databaseFormEnabled);
    }

    private void initializeQuerySelection() {
        boolean queryFormActivated = false;
        List<Query> queries = citationService.getQueries();
        LOG.debug("{} query files retrieved", queries.size());

        if (!queries.isEmpty()) {
            queryFormActivated = true;
            selectedQuery = queries.get(0);
        }

        ChoiceRenderer<Query> queryRenderer = new ChoiceRenderer<>("pid.identifier");
        DropDownChoice<Query> queryFiles = new DropDownChoice<Query>(
                "queries", new PropertyModel<Query>(this, "selectedQuery"), queries, queryRenderer);

        Form<?> queryForm = new Form<Void>("queryForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add(Constants.PID_PARAM, selectedQuery.getPid().getIdentifier());
                setResponsePage(QueryPage.class, pageParameters);

            }
        };

        add(queryForm);
        queryForm.add(queryFiles);
        queryForm.setEnabled(queryFormActivated);
    }
}
