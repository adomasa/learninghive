package distributed.monolith.learninghive.model.authority;

public enum AuthorityType {
	SELF,
	SUBORDINATE,
	SUPERVISOR,
	ADMIN;

	public static final AuthorityType[] ALL = {SELF, SUBORDINATE, SUPERVISOR, ADMIN};
}
