package distributed.monolith.learninghive.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TopicRequest extends VersionedResourceRequest {
	@Size(min = 1, max = 50, message = "Title must be 1-50 long")
	String title;

	@Size(max = 500, message = "Content must be 0-500 long")
	String content;

	Long parentId;

	List<Long> childrenId;
}