package console.person;

import java.util.UUID;

import javax.inject.Inject;

import org.axonframework.eventhandling.EventHandler;

import console.data.PersonMapper;

public class PersonEventHandler {

	private final PersonMapper mapper;

	@Inject
	public PersonEventHandler(PersonMapper mapper) {
		this.mapper = mapper;
	}

	@EventHandler
	public void create(PersonCreateEvent ev) {
		this.mapper.update(ev.getPersonId(), UUID.randomUUID().toString());
	}
}