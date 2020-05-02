package distributed.monolith.learninghive.service;

import distributed.monolith.learninghive.domain.Role;
import distributed.monolith.learninghive.domain.User;
import distributed.monolith.learninghive.model.request.UserInvitation;
import distributed.monolith.learninghive.model.request.UserRegistration;
import distributed.monolith.learninghive.model.request.UserRequest;
import distributed.monolith.learninghive.model.response.UserInfo;

import java.util.List;

public interface UserService {
	void delete(String email);

	User registerUser(String invitationToken, UserRegistration userRegistration, List<Role> roles);

	String createInvitationLink(UserInvitation userInvitation, long userId);

	UserInfo getUserInfo(Long userId);

	void updateUser(String email, UserRequest userRequest);

	List<User> getUserSubordinates(String email);

	void updateUserSupervisor(String emailSupervisor, String emailSubordinate);
}
