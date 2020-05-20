package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.model.response.TopicTree;

public interface TreeService {

	TopicTree generateUserTree(long userId);

	TopicTree generateSubordinateTree(long userId);
}
