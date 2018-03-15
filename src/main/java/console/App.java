package console;

import java.security.Principal;
import java.text.SimpleDateFormat;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.loginbox.dropwizard.mybatis.MybatisBundle;

import console.data.PersonMapper;
import console.filters.DateNotSpecifiedServletFilter;
import console.repository.CustomerRepository;
import console.repository.UserRepository;
import console.resources.ActionsResource;
import console.resources.PersonResource;
import console.resources.SecuredResource;
import console.resources.ValidInputResource;
import console.validatebundle.BasicPrincipal;
import console.validatebundle.ExampleAuthenticator;
import console.validatebundle.ExampleAuthorizer;
import console.validatebundle.JWTAuthenticator;
import console.validatebundle.JWTAuthorizer;
import console.validatebundle.MyUserPrincipal;
import console.validatebundle.OAuth2Authenticator;
import console.validatebundle.OAuth2Authorizer;
import console.validatebundle.OAuthPrincipal;
import console.webapi.AxonBundle;
import console.webapi.TestConfiguration;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.PolymorphicAuthDynamicFeature;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class App extends Application<TestConfiguration> {
	public static void main(String[] args) throws Exception {
		new App().run(args);
	}

	private final MybatisBundle<TestConfiguration> mybatisBundle = new MybatisBundle<TestConfiguration>("console") {
		@Override
		public io.dropwizard.db.DataSourceFactory getDataSourceFactory(TestConfiguration configuration) {
			return configuration.getDatabase();
		}
	};

	private final AxonBundle<TestConfiguration> axonBundle = new AxonBundle<>();

	@Override
	public void initialize(Bootstrap<TestConfiguration> bootstrap) {

		bootstrap.addBundle(new SwaggerBundle<TestConfiguration>() {
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(TestConfiguration configuration) {
				return configuration.swagger;
			}
		});
		bootstrap.addBundle(mybatisBundle);
		bootstrap.addBundle(axonBundle);
	}

	@Override
	public void run(final TestConfiguration configuration, Environment environment) throws Exception {

		// 设置验证等格式
		((DefaultServerFactory) configuration.getServerFactory()).setRegisterDefaultExceptionMappers(false);
		// 忽略JSON字符串包含多余不匹配属性反序列化
		environment.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		environment.getObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		environment.jersey().register(new DateNotSpecifiedServletFilter());

		environment.jersey().register(ActionsResource.class);
		environment.jersey().register(SecuredResource.class);
		environment.jersey().register(ValidInputResource.class);
		environment.jersey().register(PersonResource.class);

		final byte[] key = configuration.getJwtTokenSecret();

		// create the JwtConsumer instance
		final JwtConsumer consumer = new JwtConsumerBuilder()
				/*
				 * allow some leeway in validating time based claims to account
				 * for clock skew
				 */
				.setAllowedClockSkewInSeconds(30)
				/* the JWT must have an expiration time */
				.setRequireExpirationTime()
				/* the JWT must have a subject claim */
				.setRequireSubject()
				/* verify the signature with the public key */
				.setVerificationKey(new HmacKey(key))
				/* relaxes key length requirement */
				.setRelaxVerificationKeyValidation().build();

		JwtAuthFilter<?> jwtFilter = new JwtAuthFilter.Builder<MyUserPrincipal>().setJwtConsumer(consumer)
				.setCookieName("jwttoken").setAuthenticator(new JWTAuthenticator()).setAuthorizer(new JWTAuthorizer())
				.setPrefix("jwttoken").buildAuthFilter();

		AuthFilter<BasicCredentials, BasicPrincipal> basicFilter = new BasicCredentialAuthFilter.Builder<BasicPrincipal>()
				.setAuthenticator(new ExampleAuthenticator()).setAuthorizer(new ExampleAuthorizer())
				.setRealm("SUPER SECRET STUFF").buildAuthFilter();
		AuthFilter<String, OAuthPrincipal> oauthFilter = new OAuthCredentialAuthFilter.Builder<OAuthPrincipal>()
				.setAuthenticator(new OAuth2Authenticator()).setAuthorizer(new OAuth2Authorizer()).setPrefix("Bearer")
				.buildAuthFilter();
		PolymorphicAuthDynamicFeature<Principal> feature = new PolymorphicAuthDynamicFeature<>(
				ImmutableMap.of(OAuthPrincipal.class, oauthFilter, BasicPrincipal.class, basicFilter,
						MyUserPrincipal.class, jwtFilter));
		AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
				ImmutableSet.of(OAuthPrincipal.class, BasicPrincipal.class, MyUserPrincipal.class));

		// binder.bindFactory(new Factory<SecuredResource>(){
		// @Override
		// public SecuredResource provide(){
		// return new SecuredResource(configuration.getJwtTokenSecret());
		// }
		// @Override
		// public void dispose(SecuredResource instance){
		//
		// }
		// }).to(SecuredResource.class).in(PerThread.class);
		// binder.bind(SecuredResource.class).in(PerThread.class);
		// binder.bind(UserRepository.class).in(PerThread.class);

		environment.jersey().register(feature);
		environment.jersey().register(binder);
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new MyBinder());

		// 单独注册 OAuth2
		// environment.jersey()
		// .register(new AuthDynamicFeature(new
		// OAuthCredentialAuthFilter.Builder<UserPrincipal>()
		// .setAuthenticator(new OAuth2Authenticator())
		// .setAuthorizer(new OAuth2Authorizer())
		// .setPrefix("Bearer")
		// .buildAuthFilter()));
		// environment.jersey().register(RolesAllowedDynamicFeature.class);
		// //If you want to use @Auth to inject a custom Principal type into
		// your resource
		// environment.jersey().register(new
		// AuthValueFactoryProvider.Binder<>(UserPrincipal.class));
		// 单独注册jwt
		// environment.jersey().register(new AuthDynamicFeature(
		// new JwtAuthFilter.Builder<MyUserPrincipal>()
		// .setJwtConsumer(consumer)
		// .setRealm("realm")
		// .setPrefix("Bearer")
		// .setAuthenticator(new JWTAuthenticator())
		// .buildAuthFilter()));
		// environment.jersey().register(new
		// AuthValueFactoryProvider.Binder<>(Principal.class));
		// environment.jersey().register(RolesAllowedDynamicFeature.class);
	}
	
    private final class MyBinder extends AbstractBinder {
    	
        @Override
        protected void configure() {
    		bind(CustomerRepository.class).to(UserRepository.class).in(PerThread.class);
    		bindFactory(new Factory<EventBus>() {
    			@Override
    			public EventBus provide() {
    				return axonBundle.getConfiguration().eventBus();
    			}
    
    			@Override
    			public void dispose(EventBus instance) {
    
    			}
    		}).to(EventBus.class).in(PerThread.class);
    		bindFactory(new Factory<CommandGateway>() {
    			@Override
    			public CommandGateway provide() {
    				return axonBundle.getConfiguration().commandGateway();
    			}
    
    			@Override
    			public void dispose(CommandGateway instance) {
    
    			}
    		}).to(CommandGateway.class).in(PerThread.class);
    		bindFactory(new Factory<PersonMapper>() {
    			@Override
    			public PersonMapper provide() {
    				return mybatisBundle.getSqlSessionFactory().openSession().getMapper(PersonMapper.class);
    			}
    
    			@Override
    			public void dispose(PersonMapper instance) {
    
    			}
    		}).to(PersonMapper.class).in(PerThread.class);
        }
    }
}
