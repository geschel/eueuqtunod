package de.example.donutqueue;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import de.example.donutqueue.core.logic.usecase.HealthcheckLogic;
import de.example.donutqueue.db.DaoFactory;
import de.example.donutqueue.db.DaoFactoryImpl;
import de.example.donutqueue.server.resource.DeliveryCartResource;
import de.example.donutqueue.server.resource.OrdersResource;
import de.example.donutqueue.server.resource.SwaggerResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.jdbi3.bundles.JdbiExceptionsBundle;
import io.dropwizard.jersey.errors.EarlyEofExceptionMapper;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Dropwizard Server<p>
 * Please check out README.md
 */
public class Main extends Application<ConfigYaml> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Override
    public String getName() {
        return "Donut Queue - Example Microservice-API";
    }

    @Override
    public void initialize(final Bootstrap<ConfigYaml> bootstrap) {
        //basic jdbi exceptions
        bootstrap.addBundle(new JdbiExceptionsBundle());
    }

    @Override
    public void run(final ConfigYaml configuration, final Environment environment) {

        // Initialize the ConfigManager singleton with the contents of config.yml
        try {
            ConfigManager.inst().init(configuration);
        } catch (Exception e) {
            LOGGER.error("Could not initialize ConfigManager with config.yml. Exiting...");
            System.exit(0);
        }

        // Initialize database
        Jdbi jdbi;
        try {
            final JdbiFactory factory = new JdbiFactory();
            jdbi = factory.build(environment, configuration.getDatabase(), "database");
            DaoFactoryImpl.setInstance(new DaoFactoryImpl(jdbi));
            //create table 'orders'
            DaoFactoryImpl.getInstance().getOrderDao().createTable();
        } catch (Exception e) {
            // NOTE: This exception is not thrown if the config.yml has ignoreExceptionOnPreLoad: true
            LOGGER.error("Could not connect to database -> exit.");
            System.exit(0);
        }
        LOGGER.info("Database connection established.");

        // custom exception mapping and error handling
        addExceptionMapping(environment);

        // REST endpoints/resources

        DaoFactory daoFactory = DaoFactoryImpl.getInstance();
        environment.jersey().register(new DeliveryCartResource(daoFactory));
        environment.jersey().register(new OrdersResource(daoFactory));

        // Add filter that adds CORS related headers (necessary for swagger to work properly)
        addCorsFilter(configuration, environment);

        // Swagger / OpenAPI endpoint
        environment.jersey().register(new SwaggerResource().resourcePackages(
                Sets.newHashSet("de.example.donutqueue.server.resource")));

        // health checks
        environment.healthChecks().register("database_query", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                try {
                    new HealthcheckLogic(DaoFactoryImpl.getInstance()).execute();
                    return Result.healthy();
                } catch (Exception e) {
                    return Result.unhealthy(e.getMessage());
                }
            }
        });
    }

    /**
     * Adds the required "Access-Control-Allow-*" headers to the response for CORS preflight requests.
     * A request is regarded as a "preflight request" (by the CrossOriginFilter class) when it is:
     * - an OPTIONS request
     * - has the "Origin" header
     * - has at least the "Access-Control-Request-Method" header (and probably the "Access-Control-Request-Headers" header)
     * <p>
     * Allowed origins can be configured in config.yml. Use "*" to allow all origins.
     */
    private void addCorsFilter(ConfigYaml configuration, Environment environment) {

        final String goodOrigins = Joiner.on(",").join(configuration.getCorsAllowOrigins());

        // Enable CORS headers
        final FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORSFilter", CrossOriginFilter.class);

        // create URL mapping (all routes)
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        // Return "Access-Control-Allow-Origin" with the given "Origin" value if they match the configured list (or *)
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, goodOrigins);

        // Respond with this value in "Access-Control-Allow-Headers" in a preflight request (OPTIONS with "Access-Control-Request-Method")
        // Also, fail preflight (by removing all Access-Control-Allow-*"-headers) if an unknown header is delivered in "Access-Control-Request-Headers"
        filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Origin,X-User-Auth");
        // Let client access custom headers by adding "Access-Control-Expose-Headers"
        filter.setInitParameter(CrossOriginFilter.EXPOSED_HEADERS_PARAM, "X-User-Auth");
        // Respond with this value in "Access-Control-Allow-Methods" in a preflight request (OPTIONS with "Access-Control-Request-Method")
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,HEAD,OPTIONS,POST,PUT,PATCH,DELETE");

    }

    /**
     * Custom exception mapping
     *
     * @param environment - dropwizard env
     */
    private void addExceptionMapping(Environment environment) {
        // Register some default exception mappers (jersey api)
        // We disable dropwizard's default exception mappers via config.yml
        // registerDefaultExceptionMappers: false
        // but re-register the ones which make sense for us here:
        environment.jersey().register(new JerseyViolationExceptionMapper());
        environment.jersey().register(new EarlyEofExceptionMapper());
    }

    /**
     * Run with 'java -jar target/donut_queue-1.0.0.jar server config.yml'
     *
     * @param args - the usual array of strings
     */
    public static void main(final String... args) throws Exception {
        try {
            new Main().run(args);
        } catch (Exception e) {
            // Make sure the server process exits if the server can't be started
            // due to a host binding error (other server already running)
            if (e.getMessage().startsWith("java.net.BindException")) {
                System.exit(0);
            } else {
                throw e;
            }
        }
    }
}