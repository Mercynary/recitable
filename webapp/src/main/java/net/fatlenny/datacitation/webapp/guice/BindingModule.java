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
