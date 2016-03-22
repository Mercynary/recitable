package net.fatlenny.datacitation.service;

import java.io.Serializable;

import net.fatlenny.datacitation.api.PID;
import net.fatlenny.datacitation.api.Query;
import net.fatlenny.datacitation.api.Revision;

public class DefaultQuery implements Query, Serializable {
    private static final long serialVersionUID = -2932087649402009245L;

    private Revision commit;
    private String datasetName;
    private String description;
    private String query;
    private PID pid;
    private long time;

    private DefaultQuery(QueryBuilder builder) {
        this.commit = builder.commit;
        this.datasetName = builder.datasetName;
        this.description = builder.description;
        this.pid = builder.pid;
        this.query = builder.query;
        this.time = builder.time;
    }

    @Override
    public Revision getCommit() {
        return this.commit;
    }

    @Override
    public String getDatasetName() {
        return this.datasetName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public PID getPid() {
        return this.pid;
    }

    @Override
    public String getQuery() {
        return this.query;
    }
    @Override
    public long getTime() {
        return this.time;
    }

    public static class QueryBuilder {
        private Revision commit;
        private String datasetName;
        private String description;
        private PID pid;
        private String query;
        private long time;

        public QueryBuilder(PID pid, String query, String datasetName, Revision commit) {
            this.commit = commit;
            this.datasetName = datasetName;
            this.description = "";
            this.pid = pid;
            this.query = query;
            this.time = System.currentTimeMillis();
        }

        public DefaultQuery build() {
            return new DefaultQuery(this);
        }

        public QueryBuilder setTime(long time) {
            this.time = time;
            return this;
        }

        public QueryBuilder setDescription(String description) {
            this.description = description;
            return this;
        }
    }
}
