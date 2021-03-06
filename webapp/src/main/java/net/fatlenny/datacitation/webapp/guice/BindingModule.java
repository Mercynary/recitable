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
package net.fatlenny.datacitation.webapp.guice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import net.fatlenny.datacitation.api.CitationDBService;
import net.fatlenny.datacitation.service.GitCitationDBService;

public class BindingModule extends AbstractModule {
    private static Logger LOG = LoggerFactory.getLogger(BindingModule.class);

    @Override
    protected void configure() {
        bind(CitationDBService.class).to(GitCitationDBService.class);

        Names.bindProperties(binder(), loadProperties());
    }

    private Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream is =
            Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return properties;
    }

}
