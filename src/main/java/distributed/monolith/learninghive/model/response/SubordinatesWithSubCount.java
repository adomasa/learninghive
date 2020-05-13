package distributed.monolith.learninghive.model.response;

import lombok.Data;

import java.util.List;

@Data
public class SubordinatesWithSubCount {
	String topic;
	List<SubordinateLearnedCount> subordinates;
}
