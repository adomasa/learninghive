package distributed.monolith.learninghive.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubordinateLearnedCount {
	String supervisor;
	int count;
	int teamSize;
}
