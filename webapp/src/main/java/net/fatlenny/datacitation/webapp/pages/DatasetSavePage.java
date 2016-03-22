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

import java.util.UUID;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.google.inject.Inject;

import net.fatlenny.datacitation.api.CitationDBException;
import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.api.PID;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.api.Revision;
import net.fatlenny.datacitation.api.Status;
import net.fatlenny.datacitation.service.DefaultPID;
import net.fatlenny.datacitation.service.DefaultQuery;
import net.fatlenny.datacitation.service.DefaultRevision;
import net.fatlenny.datacitation.webapp.config.Constants;

public class DatasetSavePage extends WebPage {
	private static final long serialVersionUID = 1L;

    @Inject
    private CitationDBService citationDBService;

    public DatasetSavePage(final PageParameters parameters) {
		super(parameters);

        StringValue datasetParameter = parameters.get(Constants.DATASET_PARAM);
        StringValue queryParameter = parameters.get(Constants.QUERY_PARAM);
        StringValue revisonParameter = parameters.get(Constants.REV_PARAM);

        add(new BookmarkablePageLink<Void>("home", HomePage.class));

        add(new FeedbackPanel("feedback"));

        final TextField<String> datasetNameInput =
            new TextField<String>("datasetName", Model.of(datasetParameter.toString("")));
        final TextField<String> commitInput = new TextField<String>("commit", Model.of(revisonParameter.toString("")));
        final TextArea<String> queryInput =
            new TextArea<String>("queryInput", Model.of(queryParameter.toString("")));

        final TextArea<String> descriptionInput = new TextArea<String>("description", Model.of(""));
        final TextField<String> pidInput = new TextField<String>("pid", Model.of(UUID.randomUUID().toString()));

        Form<?> form = new Form<Void>("saveForm") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                String datasetString = datasetNameInput.getModelObject();
                String commitString = commitInput.getModelObject();
                String queryString = queryInput.getModelObject();

                String descriptionString = descriptionInput.getModelObject();
                descriptionString = descriptionString != null ? descriptionString : "";

                String pidString = pidInput.getModelObject();
                pidString = pidString != null ? pidString : "";

                PID pid = new DefaultPID.PIDBuilder(pidString).setName(pidString).build();
                Revision revision = new DefaultRevision(commitString);
                Query query = new DefaultQuery.QueryBuilder(pid, queryString, datasetString, revision)
                    .setDescription(descriptionString).build();

                Status status = null;
                try {
                    status = citationDBService.saveQuery(query);
                } catch (CitationDBException e) {
                    error(e);
                }

                if (status == Status.SUCCESS) {
                    info("Successfully saved query");
                }
            }

        };

        datasetNameInput.setEnabled(false);
        form.add(datasetNameInput);

        commitInput.setEnabled(false);
        form.add(commitInput);

        queryInput.setEnabled(false);
        form.add(queryInput);

        form.add(descriptionInput);
        form.add(pidInput);

        add(form);
    }
}
