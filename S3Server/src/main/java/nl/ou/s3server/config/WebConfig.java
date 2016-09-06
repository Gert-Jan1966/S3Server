package nl.ou.s3server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Deze Javaklasse bevat de Spring MVC configuratie.
 */
@Configuration
@ComponentScan(basePackages="nl.ou.s3server.controller")
public class WebConfig extends WebMvcConfigurationSupport {

}
