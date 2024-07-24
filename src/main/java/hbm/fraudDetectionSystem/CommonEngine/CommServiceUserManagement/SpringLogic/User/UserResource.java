package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User;

import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.FileConstant;
import hbm.fraudDetectionSystem.SecurityEngine.Constant.SecurityConstant;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.domain.UserPrincipal;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserNotFoundException;
import hbm.fraudDetectionSystem.SecurityEngine.Utility.JWTTokenProvider;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.HttpResponse;
import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static hbm.fraudDetectionSystem.SecurityEngine.Constant.SecurityConstant.REFRESH_TOKEN_IS_MISSING;
import static hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.constant.UserConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping(path = {"/", "/user"})
@CrossOrigin("http://localhost:4200")
public class UserResource extends ResponseResourceEntity<User> {
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse<User>> register(@RequestBody User user) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
            httpStatus = OK;
            httpMessage = USER_REGISTERED_SUCCESSFULLY;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<HttpResponse<User>> login(@RequestBody User user) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            User loginUser = userService.findByUsername(user.getUsername());
            if (loginUser == null) {
                throw new RuntimeException("Username not found");
            }
            try {
                authenticate(user.getUsername(), user.getPassword());
            } catch (AuthenticationException e) {
                this.userService.validateLoginAttempt(loginUser);
                throw e;
            }
            UserPrincipal userPrincipal = new UserPrincipal(loginUser);
            HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
            httpStatus = OK;
            httpMessage = USER_LOGIN_SUCCESSFULLY;
            return responseWithDataHeader(httpStatus, httpMessage, loginUser, jwtHeader);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewUser(@RequestBody User user) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.addNewUser(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(),
                    user.isNotLocked(), user.isActive(), user.getInstitution(), user.getType(), user.getUserGroup(), user.getRole());
            httpStatus = OK;
            httpMessage = USER_REGISTERED_SUCCESSFULLY;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestParam("currentUsername") String currentUsername,
                                        @RequestBody User user) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.updateUser(currentUsername, user.getUsername(), user.getLastName(), user.getUsername(), user.getEmail(),
                    user.isNotLocked(), user.isActive(), user.getInstitution(), user.getType(), user.getUserGroup(), user.getRole());
            httpStatus = OK;
            httpMessage = USER_UPDATED_SUCCESSFULLY;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<?> updateProfileImage(@RequestParam("username") String username,
                                                @RequestParam(value = "profileImage") MultipartFile profileImage) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.updateProfileImage(username, profileImage);
            httpStatus = OK;
            httpMessage = USER_PHOTO_UPDATED_SUCCESSFULLY;

            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(FileConstant.USER_FOLDER + username + FileConstant.FORWARD_SLASH + fileName));
    }


    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(FileConstant.TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping("/find/{username}")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<?> getUser(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UserNotFoundException(
                        String.format(
                                "User with username: %s isn't found",
                                username
                        )
                );
            }

            httpStatus = OK;
            httpMessage = USER_PHOTO_UPDATED_SUCCESSFULLY;
            return responseWithData(httpStatus, httpMessage, user);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/listUsers")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<?> getAllUsers() {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<User> fetchedData = userService.getUsers();
            httpStatus = OK;
            httpMessage = USER_FETCHED_SUCCESSFULLY;

            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @GetMapping("/listForwardUser/{username}")
//    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<?> getAllUsersExceptCurrentUserLogin(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<User> fetchedData = userService.getAllUsersExceptCurrentUserLogin(username);
            httpStatus = OK;
            httpMessage = USER_FETCHED_SUCCESSFULLY;

            return responseWithListData(httpStatus, httpMessage, fetchedData);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/resetpassword/{username}")
    public ResponseEntity<?> resetPassword(@PathVariable("username") String username, @RequestBody Map<String, String> requestData) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            authenticate(username, requestData.get("currentPass"));
            userService.resetPassword(username, requestData.get("newPass"));
            httpStatus = OK;
            httpMessage = "Password successfully changed";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/admin/resetpassword/{username}")
    public ResponseEntity<?> resetPassword(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.adminResetPassword(username);
            httpStatus = OK;
            httpMessage = "Password successfully changed";
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) throws IOException {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.deleteUser(id);
            httpStatus = OK;
            httpMessage = USER_DELETED_SUCCESSFULLY;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/activate/{username}")
    public ResponseEntity<?> activateUser(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.activateUser(username);
            httpStatus = OK;
            httpMessage = USER_ACTIVATION_SUCCESSFULLY;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/deactivate/{username}")
    public ResponseEntity<HttpResponse<User>> deactivateUser(@PathVariable("username") String username) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            userService.deactivateUser(username);
            httpStatus = OK;
            httpMessage = USER_HAS_BEEN_DEACTIVATED;
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            String authorizationToken = request.getHeader(AUTHORIZATION);
            if (authorizationToken != null && authorizationToken.startsWith(SecurityConstant.TOKEN_HEADER)) {
                String token = authorizationToken.substring(SecurityConstant.TOKEN_HEADER.length());
                String username = jwtTokenProvider.getSubject(token);
                if (jwtTokenProvider.isTokenValid(username, token)) {
                    User user = userService.findByUsername(username);
                    UserPrincipal userPrincipal = new UserPrincipal(user);
                    HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
                    return responseHeader(OK, "Token Refreshed Successfully", jwtHeader);
                } else {
                    httpStatus = UNAUTHORIZED;
                    httpMessage = "Token isn't valid";
                }
            } else {
                httpStatus = UNAUTHORIZED;
                httpMessage = REFRESH_TOKEN_IS_MISSING;
            }
            return response(httpStatus, httpMessage);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> reqBody) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<User> users = this.userService.search(reqBody);
            httpStatus = OK;
            httpMessage = USER_FETCHED_SUCCESSFULLY;
            return responseWithListData(httpStatus, httpMessage, users);
        } catch (Exception e) {
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        headers.add(SecurityConstant.JWT_REFRESH_TOKEN, jwtTokenProvider.generateRefreshToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}

