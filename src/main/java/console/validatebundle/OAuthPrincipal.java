package console.validatebundle;

import java.security.Principal;

public class OAuthPrincipal implements Principal {

	protected String personName;
	protected String userName;
	
	public void setPersonName(String personName){
		
		this.personName = personName;
	}
	
	public String getPersonName(){
		
		return this.personName;
	}
	
	public String getName() {
		return this.userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
}
