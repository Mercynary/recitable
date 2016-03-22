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
