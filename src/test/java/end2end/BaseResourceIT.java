package end2end;

import de.example.donutqueue.ConfigYaml;
import de.example.donutqueue.Main;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Starts the main service automatically before all other tests in order to allow for end-to-end testing.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class BaseResourceIT {

    public static DropwizardAppExtension<ConfigYaml> APP_EXTENSION;

    static {
        APP_EXTENSION = new DropwizardAppExtension<>(Main.class, ItUtils.CONFIG_YAML_FILE);
    }
}