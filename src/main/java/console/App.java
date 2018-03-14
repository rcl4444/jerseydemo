package console;

import java.text.SimpleDateFormat;

import javax.inject.Singleton;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.hk2.api.Factory;
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

import console.filters.DateNotSpecifiedServletFilter;
import console.repository.CustomerRepository;
import console.repository.UserRepository;
import console.resources.ActionsResource;
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
import console.webapi.TestConfiguration;
import io.dropwizard.Application;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.PolymorphicAuthDynamicFeature;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import io.swagger.converter.ModelConverters;
import io.swagger.jackson.ModelResolver;

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

	@Override
	public void initialize(Bootstrap<TestConfiguration> bootstrap) {

		bootstrap.addBundle(new ConfiguredBundle<TestConfiguration>() {
			@Override
			public void initialize(Bootstrap<?> bp) {
				bootstrap.addBundle(new ViewBundle<TestConfiguration>());
				ModelConverters.getInstance().addConverter(new ModelResolver(bootstrap.getObjectMapper()));
			}

			@Override
			public void run(TestConfiguration configuration, Environment environment) throws Exception {
				new SwaggerBundle<TestConfiguration>() {
					@Override
					protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
							TestConfiguration configuration) {
						return configuration.swagger;
					}
				}.run(configuration, environment);
			}
		});
		bootstrap.addBundle(mybatisBundle);
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

		environment.jersey().register(new ActionsResource());
		environment.jersey().register(SecuredResource.class);
		environment.jersey().register(ValidInputResource.class);

		final byte[] key = configuration.getJwtTokenSecret();

		final JwtConsumer consumer = new JwtConsumerBuilder().setAllowedClockSkewInSeconds(30) // allow
																								// some
																								// leeway
																								// in
																								// validating
																								// time
																								// based
																								// claims
																								// to
																								// account
																								// for
																								// clock
																								// skew
				.setRequireExpirationTime() // the JWT must have an expiration
											// time
				.setRequireSubject() // the JWT must have a subject claim
				.setVerificationKey(new HmacKey(key)) // verify the signature
														// with the public key
				.setRelaxVerificationKeyValidation() // relaxes key length
														// requirement
				.build(); // create the JwtConsumer instance
		//
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

		JwtAuthFilter jwtFilter = new JwtAuthFilter.Builder<MyUserPrincipal>().setJwtConsumer(consumer)
				.setCookieName("jwttoken").setAuthenticator(new JWTAuthenticator()).setAuthorizer(new JWTAuthorizer())
				.setPrefix("jwttoken").buildAuthFilter();

		AuthFilter<BasicCredentials, BasicPrincipal> basicFilter = new BasicCredentialAuthFilter.Builder<BasicPrincipal>()
				.setAuthenticator(new ExampleAuthenticator()).setAuthorizer(new ExampleAuthorizer())
				.setRealm("SUPER SECRET STUFF").buildAuthFilter();
		AuthFilter<String, OAuthPrincipal> oauthFilter = new OAuthCredentialAuthFilter.Builder<OAuthPrincipal>()
				.setAuthenticator(new OAuth2Authenticator()).setAuthorizer(new OAuth2Authorizer()).setPrefix("Bearer")
				.buildAuthFilter();
		PolymorphicAuthDynamicFeature feature = new PolymorphicAuthDynamicFeature<>(
				ImmutableMap.of(OAuthPrincipal.class, oauthFilter, BasicPrincipal.class, basicFilter,
						MyUserPrincipal.class, jwtFilter));
		AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
				ImmutableSet.of(OAuthPrincipal.class, BasicPrincipal.class, MyUserPrincipal.class));

		binder.bindFactory(new Factory<UserRepository>() {
			@Override
			public UserRepository provide() {
				return new CustomerRepository();
			}

			@Override
			public void dispose(UserRepository instance) {

			}
		}).to(UserRepository.class).in(Singleton.class);
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

		// environment.jersey().register(new AuthDynamicFeature(
		// new OAuthCredentialAuthFilter.Builder<UserPrincipal>()
		// .setAuthenticator(new OAuth2Authenticator())
		// .setAuthorizer(new OAuth2Authorizer())
		// .setPrefix("Bearer")
		// .buildAuthFilter()));
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		// //If you want to use @Auth to inject a custom Principal type into
		// your resource
		// environment.jersey().register(new
		// AuthValueFactoryProvider.Binder<>(UserPrincipal.class));
	}
}
