package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution.Institution;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType.Type;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.EmailExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserNotFoundException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException;
    List<User> getUsers();
    List<User> getAllUsersExceptCurrentUserLogin(String username);
    User findByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String firstname, String lastName, String username, String email, boolean isNotLocked, boolean isActive, Institution institution, Type type, UserGroup userGroup, Role role) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;
    User updateUser(String currentUsername, String newFirstname, String newLastname, String newUsername, String newEmail, boolean isNotLocked, boolean isActive, Institution institution, Type type, UserGroup userGroup, Role role) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;
    void deleteUser(long id) throws IOException;
    void resetPassword(String username, String newPass) throws EmailExistException;
    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException;
    void activateUser(String username);
    void deactivateUser(String username);
    User findById(long id);
    List<User> search(Map<String, Object> reqBody);
    void validateLoginAttempt(User user);
    void adminResetPassword(String username);
}
