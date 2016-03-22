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
