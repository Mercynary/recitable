package net.fatlenny.datacitation.service;

import java.io.Serializable;

import net.fatlenny.datacitation.api.PID;

public class DefaultPID implements PID, Serializable {
    private static final long serialVersionUID = 5444592685363202494L;

    private String identifier;
    private String name;

    private DefaultPID(PIDBuilder builder) {
        this.identifier = builder.identifier;
        this.name = builder.name;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static class PIDBuilder {
        private String identifier;
        private String name;

        public PIDBuilder(String identifier) {
            this.identifier = identifier;
        }

        public PIDBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public DefaultPID build() {
            return new DefaultPID(this);
        }
    }
}
