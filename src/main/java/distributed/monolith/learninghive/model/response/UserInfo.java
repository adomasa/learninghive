package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import distributed.monolith.learninghive.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
	long id;
	String email;
	String name;
	String surname;
	String role;
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	User supervisor;
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	List<User> subordinates;
}
