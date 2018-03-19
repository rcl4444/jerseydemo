package console.webapi;

import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;

import console.data.PersonMapper;
import console.person.PersonCommandHandler;
import console.person.PersonEventHandler;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AxonBundle<T extends io.dropwizard.Configuration> implements ConfiguredBundle<T> {

	protected Configurer configurer;
	protected Configuration configuration;
	protected ServiceLocator serviceLocator;

	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void run(T configuration, Environment environment) throws Exception {
		this.serviceLocator = (ServiceLocator) environment.getApplicationContext()
				.getAttribute(ServletProperties.SERVICE_LOCATOR);
	}

	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		this.configurer = DefaultConfigurer.defaultConfiguration();
		this.configuration = this.configurer.buildConfiguration();
		configurer.registerCommandHandler(
				c -> new PersonCommandHandler(this.serviceLocator.create(PersonMapper.class), c.eventBus()));
		EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
				.registerEventHandler(conf -> new PersonEventHandler(this.serviceLocator.create(PersonMapper.class)));

		// the module needs to be registered with the Axon Configuration
		this.configurer.registerModule(ehConfiguration);
	}

}