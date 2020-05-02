package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.request.TrainingDayRequest;
import distributed.monolith.learninghive.model.response.TrainingDayResponse;

import java.util.List;

public interface TrainingDayService {
	TrainingDayResponse addTrainingDay(TrainingDayRequest trainingDayRequest);

	TrainingDayResponse updateTrainingDay(long trainingDayId, TrainingDayRequest trainingDayRequest);

	List<TrainingDayResponse> queryTrainingDays(long userId);

	void deleteTrainingDay(long id);
}
