package net.fatlenny.datacitation.webapp.config;

import java.io.File;
import java.util.Properties;

public interface ConfigService {
    Properties loadConfig(File file);
}
