package console.person;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {
	long id;
	String name;
	int aget;
}