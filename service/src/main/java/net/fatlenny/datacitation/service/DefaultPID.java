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
