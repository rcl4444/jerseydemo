package console.person;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PersonCreateCommand {
	String name;
	Integer age;
}