package net.fatlenny.datacitation.api;

import java.util.List;

public interface CitationDBService {
    /**
     * Retrieves a list of data set names. If no sets exist the list is {@code empty}.
     * 
     * @return a list of table names.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    List<String> getDatasetNames() throws CitationDBException;

    /**
     * Loads a data set from the with the passed {@code name}. If no data set with the passed {@code name } exists the
     * list is {@code empty}.
     * 
     * @param datasetName the name of the data set. <strong>The name is case sensitive.</strong>
     * @return a {@link TableModel} containing the data set.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    TableModel loadDataset(String datasetName) throws CitationDBException;

    /**
     * Retrieves a list of {@link Query}s. If there are no {@link Query}s the list is {@code empty}.
     * 
     * @return a list of {@link Query}s.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    List<Query> getQueries() throws CitationDBException;

    /**
     * Retrieves a specific query identified by the {@code pid}. If the query doesn't exist an empty query is returned.
     * 
     * @param pid the persistent identifier of the query.
     * @return the {@link Query} identified by the {@code pid}.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    Query getQueryById(String pid) throws CitationDBException;

    /**
     * Retrieves a result of a specific {@link Query}.
     * 
     * @param query the {@link Query} that is used to retrieve the filtered data set.
     * @return the {@link TableModel} containing the filtered data set.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    TableModel getQueryResult(Query query) throws CitationDBException;

    /**
     * Saves a query and returns resulting status.
     * 
     * @param query the {@link Query} to save.
     * @return either {@link Status#SUCCESS} or {@link Status#ERROR}.
     * @throws CitationDBException in case something went wrong and could not be recovered.
     */
    Status saveQuery(Query query) throws CitationDBException;
}
