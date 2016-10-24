/**
 * 
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;
import de.evoila.cf.cpi.custom.props.MongoDBCustomPropertyHandler;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@SpringBootApplication
public class Application {

	@Value("${mongodb.security.key.length}")
	private int keyLength;

	@Bean(name = "customProperties")
	public Map<String, String> customProperties() {
		Map<String, String> customProperties = new HashMap<String, String>();
		customProperties.put("database_name", "admin");

		return customProperties;
	}

	@Bean
	public DomainBasedCustomPropertyHandler domainPropertyHandler() {
		return new MongoDBCustomPropertyHandler(keyLength);
	}

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		ApplicationContext ctx = springApplication.run(args);

		Assert.notNull(ctx);
	}

}