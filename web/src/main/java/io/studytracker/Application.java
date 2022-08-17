package io.studytracker;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@SpringBootApplication(
		exclude = {
				UserDetailsServiceAutoConfiguration.class,
				ElasticsearchRepositoriesAutoConfiguration.class,
				ElasticsearchRestClientAutoConfiguration.class
		}
	)
@PropertySource("classpath:defaults.properties")
public class Application {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
				.allowCircularReferences(true)
				.run(args);
	}

}
