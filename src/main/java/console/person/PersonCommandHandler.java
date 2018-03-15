package console.person;

import javax.inject.Inject;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;

import console.data.PersonMapper;

public class PersonCommandHandler {

	private final PersonMapper mapper;
	private final EventBus eventBus;

	@Inject
	public PersonCommandHandler(PersonMapper mapper, EventBus eventBus) {
		this.mapper = mapper;
		this.eventBus = eventBus;
	}

	@CommandHandler
	public boolean create(PersonCreateCommand cmd) {
		Person p = Person.builder().name(cmd.getName()).aget(cmd.getAge()).build();
		this.mapper.insert(p);
		this.eventBus.publish(new GenericEventMessage<>(PersonCreateEvent.builder().personId(p.getId()).build()));
		return true;
	}
}