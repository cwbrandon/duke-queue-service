package edu.duke.bioinformatics;

import static edu.duke.bioinformatics.Constants.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Application.class, args);
    }

    /*
    @Bean
    DataSource dataSource() throws URISyntaxException
    {
        DataSource dataSource = null;
        final String databaseUrl = System.getenv(DATABASE_URL);
        if (StringUtils.isNotBlank(databaseUrl))
        {
            logger.info("-- using database url={} --", databaseUrl);

            URI dbUri = new URI(databaseUrl);
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);

            if (dbUri.getUserInfo() != null)
            {
                config.setUsername(dbUri.getUserInfo().split(":")[0]);
                config.setPassword(dbUri.getUserInfo().split(":")[1]);
            }

            final String poolSize = System.getenv(DATABASE_POOL_SIZE);
            logger.info("-- database pool size={} --", poolSize);
            config.setMaximumPoolSize(Integer.valueOf(poolSize));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            dataSource = new HikariDataSource(config);
        }
        else
        {
            logger.info("-- not using dataSource, no database url specified --");
        }
        return dataSource;
    }
    */
}
