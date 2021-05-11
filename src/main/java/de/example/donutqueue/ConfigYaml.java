package de.example.donutqueue;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents server configuration file 'config.yml'
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Getter
@Setter
public class ConfigYaml extends Configuration {

    @NotNull
    private List<String> corsAllowOrigins;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

}