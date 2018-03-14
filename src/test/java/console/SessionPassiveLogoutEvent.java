package console;

import lombok.Data;

@Data
public class SessionPassiveLogoutEvent {

	final String oldsesssionid;
	final LoginContext context;
}