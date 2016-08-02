package edu.duke.bioinformatics.config;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;

import static edu.duke.bioinformatics.Constants.API_VERSION;
import static edu.duke.bioinformatics.Constants.SERVICE_NAME;

/**
 * See http://swagger.io/ for more info
 * This provides the /api-docs/ json metadata controller
 * The swagger-ui exists completely as static html/js in src/main/webapp
 */
@Configuration
@EnableSwagger
public class SwaggerConfig
{
    private static final String API_NAME = SERVICE_NAME + " API";

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    /**
     * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
     * swagger groups i.e. same code base multiple swagger resource listings.
     * <p>
     * accessed by /api-docs (in other words, the default group)
     */
    @Bean
    public SwaggerSpringMvcPlugin publicApi() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .apiVersion(API_VERSION)
                .includePatterns(".*?")
                .ignoredParameterTypes(Principal.class);
    }


    private ApiInfo apiInfo() {
        return new ApiInfo(
                //title
                API_NAME,
                //description
                "Allows the producing and consuming of messages from a queue",
                //terms of service
                null,
                //contact email
                null,
                //license type
                null,
                //license url
                null
        );
    }

}
