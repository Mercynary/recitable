package net.fatlenny.datacitation.api;

import java.util.List;

public interface TableModel {
    List<String> getHeaderData();

    TableModelMetaData getMetaData();

    List<String[]> getRowData();
}
