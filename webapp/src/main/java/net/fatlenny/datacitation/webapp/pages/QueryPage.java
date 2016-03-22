package net.fatlenny.datacitation.webapp.pages;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.api.TableModel;
import net.fatlenny.datacitation.webapp.config.Constants;

public class QueryPage extends WebPage {
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(QueryPage.class);

    @Inject
    private CitationDBService citationDBService;

    public QueryPage(final PageParameters parameters) {
		super(parameters);

        StringValue selectedQueryPid = parameters.get(Constants.PID_PARAM);

        add(new BookmarkablePageLink<Void>("home", HomePage.class));

        add(new FeedbackPanel("feedback"));

        if (selectedQueryPid.isEmpty()) {
            error("No query transmitted. Please go back and select a valid query.");
            LOG.error("Parameter 'selectedQueryPid' null or emtpy.");
        }

        Query query = citationDBService.getQueryById(selectedQueryPid.toString());
        TableModel tableModel = citationDBService.getQueryResult(query);

        final TextArea<String> queryString =
            new TextArea<String>("queryString", Model.of(wrapLines(query.getQuery())));
        queryString.setEnabled(false);
        add(queryString);

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
    }

    private String wrapLines(String query) {
        String[] splitArray = query.split("\\s");

        StringBuffer textBuffer = new StringBuffer(query.length());
        StringBuffer lineBuffer = new StringBuffer(0);

        for (String queryToken : splitArray) {
            int stringBufferLength = lineBuffer.length();
            int stringLength = queryToken.length();
            boolean lineLength = (stringBufferLength + stringLength + 1) < 80;
            if (lineLength) {
                lineBuffer.append(queryToken).append(" ");
            } else {
                textBuffer.append(lineBuffer.toString()).append("\n");
                lineBuffer = new StringBuffer(0).append(queryToken).append(" ");
            }
        }
        textBuffer.append(lineBuffer.toString());

        return textBuffer.toString();
    }

}
