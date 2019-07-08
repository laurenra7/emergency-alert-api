package edu.byu.emergency.config;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configure the DataSource. Spring's JdbcTemplate, which is the main class
 * for querying the database, automatically uses the DataSource bean. This
 * configuration overrides the default Spring Boot configuration which you
 * can read about here:
 *
 * https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-sql.html
 * https://www.baeldung.com/spring-boot-configure-data-source-programmatic
 *
 * By default, Spring Boot will try to use these connection pools if it can
 * find the library (in this order): HikariCP, Tomcat, Commons DBCP.
 *
 * Note: if the same properties are defined in multiple PropertySource
 * locations, the subsequent ones are used. The last one "wins". If the
 * uPortal properties file which is last in the list (global.properties) is
 * found on the server, it will use the database settings from there, otherwise
 * it will fall back to the prior ones (datasource.properties) in the list.
 */
@Configuration
@PropertySource({"classpath:datasource.properties",
        "file:${PORTAL_HOME}/global.properties"
})
public class DataSourceConfig {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Value("${hibernate.connection.driver_class}")
    private String dbDriverClass;

    @Value("${hibernate.connection.url}")
    private String dbUrl;

    @Value("${hibernate.connection.username}")
    private String dbUser;

    @Value("${hibernate.connection.password}")
    private String dbPswd;

    @Bean
    public DataSource getDataSource() {
        log.debug("JDBC connected to database " + dbUrl + " using driver " + dbDriverClass);
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(dbDriverClass);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUser);
        dataSourceBuilder.password(dbPswd);
        return dataSourceBuilder.build();
    }

}
