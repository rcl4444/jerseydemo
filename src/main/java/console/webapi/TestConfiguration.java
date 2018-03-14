package console.webapi;

import java.io.UnsupportedEncodingException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dhatim.dropwizard.jwt.cookie.authentication.JwtCookieAuthConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class TestConfiguration extends Configuration {

	public SwaggerBundleConfiguration swagger ;
	
	@Valid
	@NotNull
	private JwtCookieAuthConfiguration jwtCookieAuth = new JwtCookieAuthConfiguration();
	
	public JwtCookieAuthConfiguration getJwtCookieAuth() {
		  return jwtCookieAuth;
	}
	
	@NotEmpty
    private String jwtTokenSecret = "dfwzsdzwh823zebdwdz772632gdsbdab";

    public byte[] getJwtTokenSecret() {
        try {
			return jwtTokenSecret.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
    @Valid
    @NotNull
	private DataSourceFactory database = new DataSourceFactory();
    
    public DataSourceFactory getDatabase(){
    	return this.database;
    }
}
