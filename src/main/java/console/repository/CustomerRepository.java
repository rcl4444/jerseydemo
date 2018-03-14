package console.repository;

import java.util.UUID;

//@Service
public class CustomerRepository implements UserRepository {

	final String flag;
	
	public CustomerRepository(){
		this.flag = UUID.randomUUID().toString();
	}
	
	@Override
	public int countUser() {
		return 10;
	}

	@Override
	public String getUniqueFlag() {
		return this.flag;
	}
}
