package console;

import java.security.Principal;
import java.util.List;

import lombok.Data;

@Data
public class LoginContext implements Principal {

	final String sessionId;
	final List<MenuData> modules;
	final Integer isInitLogin;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
