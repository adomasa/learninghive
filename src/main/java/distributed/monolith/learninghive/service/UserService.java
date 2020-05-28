package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.InvitationData;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.response.UserInfo;

import java.util.List;

public interface UserService {
	void delete(long userId);

	User registerUser(String invitationToken, UserRegistration userRegistration, Role role);

	InvitationData createInvitation(UserInvitation userInvitation, long userId);

	UserInfo getUserInfo(long userId);

	List<UserInfo> getUserSubordinates(long userId);

	List<UserInfo> findTeamMembers(long userId);

	void updateUserSupervisor(long supervisorId, long subordinateId);
}
