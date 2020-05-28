package distributed.monolith.learninghive.model.response.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubordinatesWithSubCount {
	String topic;
	List<SubordinateLearnedCount> subordinates;
}
