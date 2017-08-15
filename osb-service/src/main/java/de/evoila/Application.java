/**
 * 
 */
package de.evoila;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.cloud.bus.BusAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import de.evoila.cf.broker.bean.MongoDBSecurityKeyBean;
import de.evoila.cf.cpi.custom.props.DomainBasedCustomPropertyHandler;
import de.evoila.cf.cpi.custom.props.MongoDBCustomPropertyHandler;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@RefreshScope
@SpringBootApplication
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class, BusAutoConfiguration.class})
public class Application {

	private int keyLength;

	@Autowired
	private MongoDBSecurityKeyBean mongodbSecurityKeyBean;
	
	@PostConstruct
	private void initValues() {
		keyLength = mongodbSecurityKeyBean.getLength();
	}
	
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

		Assert.notNull(ctx, "ApplicationContext must not be null.");
	}

}