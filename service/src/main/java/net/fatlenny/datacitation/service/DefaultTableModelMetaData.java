package net.fatlenny.datacitation.service;

import net.fatlenny.datacitation.api.Revision;
import net.fatlenny.datacitation.api.TableModelMetaData;

public class DefaultTableModelMetaData implements TableModelMetaData {
    private Revision revision;

    public DefaultTableModelMetaData(Revision revision) {
        this.revision = revision;
    }

    @Override
    public Revision getRevision() {
        return revision;
    }
}
