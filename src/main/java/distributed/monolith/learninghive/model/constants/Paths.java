package distributed.monolith.learninghive.model.constants;

public final class Paths {
	public static final String ACCOUNT_REFRESH = "/account/token/refresh";
	public static final String ACCOUNT_LOGIN = "/account/login";
	public static final String ACCOUNT_REGISTER = "/account/register";
	public static final String ACCOUNT_LOGOUT = "/account/logout";
	public static final String ACCOUNT_INFO = "/account/info";
	public static final String ACCOUNT_INVITE = "/account/invite";

	public static final String TOPIC_ADD = "/topic/add";
	public static final String TOPIC_QUERY = "/topic/q";
	public static final String TOPIC_DELETE = "/topic/delete";
	public static final String TOPIC_UPDATE = "/topic/update";
	public static final String TOPIC_CREATE_LEARNED = "/topic/createLearned";
	public static final String TOPIC_DELETE_LEARNED = "/topic/deleteLearned";
	public static final String TOPIC_QUERY_LEARNED = "/topic/queryLearned";

	public static final String OBJECTIVE_ADD = "/objective/add";
	public static final String OBJECTIVE_QUERY = "/objective/q";
	public static final String OBJECTIVE_DELETE = "/objective/delete";
	public static final String OBJECTIVE_UPDATE = "/objective/update";

	public static final String RESTRICTIONS_ADD = "/restriction/add";
	public static final String RESTRICTIONS_QUERY = "/restriction/q";
	public static final String RESTRICTIONS_DELETE = "/restriction/delete";
	public static final String RESTRICTIONS_COPY = "/restriction/copyToTeam";
	public static final String RESTRICTIONS_UPDATE = "/restriction/update";

	public static final String USER_DELETE = "/user/delete";
	public static final String USER_UPDATE = "/user/update";
	public static final String USER_QUERY = "/user/q";
	public static final String USER_SUPERVISOR = "/user/supervisor";

	public static final String TRAINING_DAY_ADD = "/trainingDay/add";
	public static final String TRAINING_DAY_QUERY = "/trainingDay/q";
	public static final String TRAINING_DAY_DELETE = "/trainingDay/delete";
	public static final String TRAINING_DAY_UPDATE = "/trainingDay/update";


	private Paths() {
	}
}
