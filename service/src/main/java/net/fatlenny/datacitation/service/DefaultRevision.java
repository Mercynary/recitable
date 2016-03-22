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
