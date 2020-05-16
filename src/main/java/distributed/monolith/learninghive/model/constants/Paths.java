package distributed.monolith.learninghive.model.constants;

public final class Paths {
	public static final String ACCOUNT_REFRESH = "/account/token/refresh";
	public static final String ACCOUNT_LOGIN = "/account/login";
	public static final String ACCOUNT_REGISTER = "/account/register";
	public static final String ACCOUNT_LOGOUT = "/account/logout";
	public static final String ACCOUNT_INVITE = "/account/invite";
	public static final String ACCOUNT = "/account";

	public static final String TOPIC = "/topic";
	public static final String LEARNED_TOPIC = "/topic/learned";

	public static final String OBJECTIVE = "/objective";

	public static final String RESTRICTIONS = "/restriction/add";
	public static final String RESTRICTIONS_COPY = "/restriction/copyToTeam";

	public static final String USER = "/user";
	public static final String USER_SUBORDINATES = "/user/subordinates";
	public static final String USER_TEAM = "/user/team";
	public static final String USER_SUPERVISOR = "/user/supervisor";

	public static final String TRAINING_DAY = "/trainingDay";

	public static final String STATS_EMPLOYEES = "/stats/employees";
	public static final String STATS_SUBORDINATES = "/stats/subordinates";
	public static final String STATS_PROGRESS = "/stats/progress";

	private Paths() {
	}
}
