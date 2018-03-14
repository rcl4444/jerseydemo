package console.validatebundle;

import java.security.Principal;

public class BasicPrincipal implements Principal {

	protected String address;
	protected String userName;
	
	public void setAddress(String address){
		
		this.address = address;
	}
	
	public String getAddress(){
		
		return this.address;
	}
	
	public String getName() {
		return this.userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}
}
