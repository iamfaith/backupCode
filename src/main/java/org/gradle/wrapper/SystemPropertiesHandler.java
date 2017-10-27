package org.gradle.wrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemPropertiesHandler {
    public static Map<String, String> getSystemProperties(File propertiesFile) {
        FileInputStream inStream;
        Map<String, String> propertyMap = new HashMap();
        if (propertiesFile.isFile()) {
            Properties properties = new Properties();
            try {
                inStream = new FileInputStream(propertiesFile);
                properties.load(inStream);
                inStream.close();
                Pattern pattern = Pattern.compile("systemProp\\.(.*)");
                for (Object argument : properties.keySet()) {
                    Matcher matcher = pattern.matcher(argument.toString());
                    if (matcher.find()) {
                        String key = matcher.group(1);
                        if (key.length() > 0) {
                            propertyMap.put(key, properties.get(argument).toString());
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error when loading properties file=" + propertiesFile, e);
            } catch (Throwable th) {
                inStream.close();
            }
        }
        return propertyMap;
    }
}
