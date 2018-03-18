package console.webapi;

import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;

import console.person.PersonEventHandler;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class AxonBundle<T extends io.dropwizard.Configuration> implements ConfiguredBundle<T> {

	protected Configurer configurer;

	protected Configuration configuration;

	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	public void run(T configuration, Environment environment) throws Exception {
		this.configuration = this.configurer.buildConfiguration();
	}

	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		this.configurer = DefaultConfigurer.defaultConfiguration();
		// configurer.registerCommandHandler(c -> new PersonCommandHandler());
		// EventHandlingConfiguration ehConfiguration = new EventHandlingConfiguration()
		// 		.registerEventHandler(conf -> new PersonEventHandler());

		// // the module needs to be registered with the Axon Configuration
		// configurer.registerModule(ehConfiguration);
	}

}