package util;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class for retrieving property values
 */
public class ApplicationProperties {

    ////////////////////////////////////////////
    // ATTRIBUTES
    
    private static Properties props;

    /////////////////////////////////////////////
    // METHODS

    /**
     * Loads properties from the given file
     */
    public static void loadProperties(String propFileName) {
        props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(propFileName);
            props.load(fis);
            fis.close();
        }
        catch (Exception e) {
            System.out.println("Could not find.load property file[" + propFileName + "]: " + e);
            System.exit(1);
        }
    }

    /**
     * Returns the value for given key
     * @param key
     * @return value for given key
     */
    public static String getPropertyValue(String key) {
        return props.getProperty(key);
    }   
}
