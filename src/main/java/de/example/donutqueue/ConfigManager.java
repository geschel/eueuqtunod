package de.example.donutqueue;

/**
 * Thread-safe singleton that allows global access to config variables.
 */
public class ConfigManager {

    private static ConfigManager INSTANCE;

    // Synchronisation locks
    private static final Object instantiationLock = new Object();
    private static final Object initializationLock = new Object();

    private ConfigYaml yml;

    private ConfigManager() {
    }

    /**
     * @return Singleton instance of ConfigManager
     */
    public static ConfigManager inst() {
        ConfigManager out = INSTANCE;
        if (out == null) {
            synchronized (instantiationLock) {
                out = INSTANCE;
                if (out == null) {
                    INSTANCE = out = new ConfigManager();
                }
            }
        }
        return out;
    }

    /**
     * Populates the singleton with its data payload. Can only be called once.
     *
     * @param yml - our global ConfigYaml object
     */
    void init(ConfigYaml yml) {
        synchronized (initializationLock) {
            if (null == this.yml) {
                this.yml = yml;
            }
        }
    }

    /**
     * @return Reference to the application's ConfigYaml object
     */
    public ConfigYaml getYml() throws RuntimeException {
        if (null == yml) {
            throw new RuntimeException("ConfigManager yml accessed before it has been initialized.");
        } else {
            return yml;
        }
    }
}
