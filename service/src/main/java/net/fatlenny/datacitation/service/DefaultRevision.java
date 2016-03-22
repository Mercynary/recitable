package net.fatlenny.datacitation.service;

import java.io.Serializable;

import net.fatlenny.datacitation.api.Revision;

public class DefaultRevision implements Revision, Serializable {
    private static final long serialVersionUID = -4174551628856526832L;
    public static final DefaultRevision ZEROID;

    static {
        ZEROID = new DefaultRevision("0");
    }

    private String id;

    public DefaultRevision(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public String getRevisionId() {
        return this.id;
    }
}
