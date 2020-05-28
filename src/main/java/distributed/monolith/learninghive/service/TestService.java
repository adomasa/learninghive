package distributed.monolith.learninghive.service;

public interface TestService {
	void addTestData();

	void addTestUsers(Long loggedUserId);

	void addTestTopicsToUsers();
}
