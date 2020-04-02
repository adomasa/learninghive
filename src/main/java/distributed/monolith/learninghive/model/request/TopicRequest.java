package distributed.monolith.learninghive.model.request;

import lombok.Value;

import javax.validation.constraints.Size;
import java.util.List;

@Value
public class TopicRequest {
	@Size(min = 1, max = 50, message = "Title must be 1-50 long")
	String title;

	// todo additional constraints?
	@Size(max = 500, message = "Content must be 0-500 long")
	String content;

	Long parentId;

	List<Long> childrenId;
}