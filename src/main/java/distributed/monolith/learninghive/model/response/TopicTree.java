package distributed.monolith.learninghive.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TopicTree {
	String name;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	long id;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<TopicTree> children = new ArrayList<>();

	public void addToList(final TopicTree node) {
		this.children.add(node);
	}
}
