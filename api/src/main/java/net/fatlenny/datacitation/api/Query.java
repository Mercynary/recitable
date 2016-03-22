package net.fatlenny.datacitation.api;

/**
 * Represents a query and its attributes.
 */
public interface Query {
    /**
     * Returns the {@code revision} that the query is bound to.
     * 
     * @return the {@link Revision} bound to the query.
     */
    Revision getCommit();

    /**
     * Returns the {@code dataset name} that the query is run on.
     * 
     * @return the {@code dataset name}.
     */
    String getDatasetName();

    /**
     * Returns a {@code description} for the query.
     * 
     * @return the {@code description}.
     */
    String getDescription();

    /**
     * Returns the Persistent Identifier that corresponds to the query.
     * 
     * @return the {@link PID} for the query.
     */
    PID getPid();

    /**
     * Returns the actual {@code query} that is run on the specified data set.
     * 
     * @return the {@code query}.
     */
    String getQuery();

    /**
     * Returns the time the query was created.
     * 
     * @return the query creation time.
     */
    long getTime();
}
