package console.person;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonCreateEvent {
	long personId;
}