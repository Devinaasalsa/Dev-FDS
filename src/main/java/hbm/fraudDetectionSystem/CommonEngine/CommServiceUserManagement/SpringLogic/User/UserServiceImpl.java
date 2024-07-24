package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution.Institution;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Institution.InstitutionService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.Role;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.Role.RoleService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroup;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup.UserGroupService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType.Type;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserType.UserTypeService;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.FileConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.domain.UserPrincipal;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.EmailExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserNotFoundException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UsernameExistException;
import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification.EmailServiceImpl;
import hbm.fraudDetectionSystem.SecurityEngine.Service.LoginAttemptService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {
    private Logger LOGGER = LoggerFactory.getLogger("DEF-U");
    @PersistenceContext
    private EntityManager em;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private final InstitutionService institution;
    private final UserTypeService type;
    private final UserGroupService group;
    private final RoleService role;
    private final EmailServiceImpl emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService, InstitutionService institutionService, InstitutionService institution, UserTypeService type, UserGroupService group, RoleService role, EmailServiceImpl emailService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.institution = institution;
        this.type = type;
        this.group = group;
        this.role = role;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            LOGGER.error("User Not Found by username: " + username);
            throw new UsernameNotFoundException("User Not Found by username: " + username);
        } else {
//            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
//            LOGGER.info("Returning found user By Username : " + username);
            return userPrincipal;
        }
    }

    @Override
    public void validateLoginAttempt(User user) {
        if (user.isNotLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getUsername())) {
                user.setNotLocked(false);
            } else {
                user.setNotLocked(true);
            }
            userRepository.save(user);
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public void adminResetPassword(String username) {
        User currentUser = this.findByUsername(username);

        if (currentUser == null) {
            throw new RuntimeException("Username not found");
        }
        String password = generatePassword();

        currentUser.setResetPassword(true);
        currentUser.setPassword(encodePassword(password));

        LOGGER.info(
                String.format(
                        "New user password: \n\tUsername: [%s] \n\tPassword: [%s]",
                        username, password
                )
        );

        Thread emailThread = new Thread(() -> emailService.sendDefaultPass(currentUser.getEmail(), username, password));
        emailThread.start();

        this.userRepository.save(currentUser);
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
//        user.setInstitution(institution.findInstitutionByInstitutionName("RPL"));
        user.setType(type.findTypeByTypeName("TYPE_USER"));
//        user.setUserGroup(group.findUserGroupByGroupName("Employee"));
        user.setRole(role.findRoleByRoleName("ROLE_USER"));
        user.setResetPassword(true);
        userRepository.save(user);
        LOGGER.info(
                String.format(
                        "New user password: \n\tUsername: [%s] \n\tPassword: [%s]",
                        username, password
                )
        );

        return user;
    }

    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(Integer.parseInt("10"));
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(Integer.parseInt("10"));
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User userByNewUsername = findByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);

        if (StringUtils.isNotBlank(currentUsername)) {
            User currentUser = findByUsername(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(UserConstant.NO_USER_FOUND_BY_USERNAME + currentUsername);
            }
            if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
                throw new UsernameExistException(UserConstant.USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(UserConstant.EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        } else {
            if (userByNewUsername != null) {
                throw new UsernameExistException(UserConstant.USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(UserConstant.EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findByOrderByIdAsc();
    }

    @Override
    public List<User> getAllUsersExceptCurrentUserLogin(String username) {
        return userRepository.findAllByUsernameNotIn(List.of(username));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User addNewUser(String firstname, String lastName, String username, String email, boolean isNotLocked, boolean isActive, Institution institution, Type type, UserGroup userGroup, Role role) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setResetPassword(true);
        user.setActive(isActive);
        user.setNotLocked(isNotLocked);
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        user.setInstitution(institution);
        user.setType(type);
        user.setUserGroup(userGroup);
        user.setRole(role);
        userRepository.save(user);
        LOGGER.info(
                String.format(
                        "New user password: \n\tUsername: [%s] \n\tPassword: [%s]",
                        username, password
                )
        );

        Thread emailThread = new Thread(() -> emailService.sendDefaultPass(email, username, password));
        emailThread.start();

//        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public User updateUser(String currentUsername, String newFirstname, String newLastname, String newUsername, String newEmail, boolean isNotLocked, boolean isActive, Institution institution, Type type, UserGroup userGroup, Role role) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
        currentUser.setFirstName(newFirstname);
        currentUser.setLastName(newLastname);
        currentUser.setJoinDate(new Date());
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNotLocked);
        currentUser.setInstitution(institution);
        currentUser.setType(type);
        currentUser.setUserGroup(userGroup);
        currentUser.setRole(role);
        userRepository.save(currentUser);
//        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null) {
            Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                LOGGER.info(FileConstant.DIRECTORY_CREATED);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + FileConstant.DOT + FileConstant.PNG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + FileConstant.DOT + FileConstant.PNG_EXTENSION), REPLACE_EXISTING);
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            LOGGER.info(FileConstant.FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.USER_IMAGE_PATH + username + FileConstant.FORWARD_SLASH + username + FileConstant.DOT + FileConstant.PNG_EXTENSION).toUriString();
    }

    @Override
    public void deleteUser(long id) throws IOException {
        User user = userRepository.findAllById(id);
        Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
        FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String username, String newPass) throws EmailExistException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new EmailExistException(UserConstant.NO_USER_FOUND_BY_USERNAME + username);
        }
        user.setResetPassword(false);
        user.setPassword(encodePassword(newPass));
        userRepository.save(user);
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

    @Override
    public void activateUser(String username) {
        User activateUser = findByUsername(username);
        activateUser.setActive(true);
//        LOGGER.info("User by username : " + activateUser.getUsername() + " has been activate");
    }

    @Override
    public void deactivateUser(String username) {
        User deactivateUser = findByUsername(username);
        deactivateUser.setActive(false);
//        LOGGER.info("User by username : " + deactivateUser.getUsername() + " has been deactivate");
    }

    @Override
    public User findById(long id) {
        return userRepository.findAllById(id);
    }

    @Override
    public List<User> search(Map<String, Object> reqBody) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        reqBody
                .forEach((key, value) -> {
                    if (value != null)
                        if (!value.toString().isEmpty()) {
                            switch (key) {
                                case "institution":
                                    Long institutionId = Long.parseLong(value.toString());
                                    Join<User, Institution> institutionJoin = root.join("institution");
                                    predicates.add(cb.equal(institutionJoin.get("id"), institutionId));
                                    break;

                                case "type":
                                    Long typeId = Long.parseLong(value.toString());
                                    Join<User, Type> typeJoin = root.join("type");
                                    predicates.add(cb.equal(typeJoin.get("id"), typeId));
                                    break;

                                case "userGroup":
                                    Long userGroupId = Long.parseLong(value.toString());
                                    Join<User, UserGroup> userGroupJoin = root.join("userGroup");
                                    predicates.add(cb.equal(userGroupJoin.get("id"), userGroupId));
                                    break;

                                case "role":
                                    Long roleId = Long.parseLong(value.toString());
                                    Join<User, Role> roleJoin = root.join("role");
                                    predicates.add(cb.equal(roleJoin.get("roleId"), roleId));
                                    break;

                                default:
                                    predicates.add(cb.equal(root.get(key), value));
                                    break;
                            }
                        }
                });

        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<User> typedQuery = this.em.createQuery(query);
        return typedQuery.getResultList();
    }
}
