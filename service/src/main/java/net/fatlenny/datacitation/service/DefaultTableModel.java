package net.fatlenny.datacitation.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.fatlenny.datacitation.api.TableModel;
import net.fatlenny.datacitation.api.TableModelMetaData;

public class DefaultTableModel implements TableModel, Serializable {
    private static final long serialVersionUID = 8801369563211271266L;

    private List<String> headerData;
    private List<String[]> rowData;
    private TableModelMetaData metaData;

    public DefaultTableModel(TableModelMetaData metaData, List<String> header) {
        this.metaData = metaData;
        this.headerData = header;
        this.rowData = new ArrayList<>();
    }

    public boolean addRow(String[] row) {
        if (headerData.size() != row.length) {
            return false;
        }
        return rowData.add(row);
    }

    @Override
    public List<String> getHeaderData() {
        return this.headerData;
    }

    @Override
    public TableModelMetaData getMetaData() {
        return this.metaData;
    }

    @Override
    public List<String[]> getRowData() {
        return this.rowData;
    }
}
