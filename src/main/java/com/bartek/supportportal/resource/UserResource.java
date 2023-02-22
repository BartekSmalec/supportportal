package com.bartek.supportportal.resource;

import com.bartek.supportportal.domain.HttpResponse;
import com.bartek.supportportal.domain.User;
import com.bartek.supportportal.domain.UserPrincipal;
import com.bartek.supportportal.exception.ExceptionHandling;
import com.bartek.supportportal.exception.domain.EmailExistException;
import com.bartek.supportportal.exception.domain.EmailNotFoundException;
import com.bartek.supportportal.exception.domain.UserNotFoundException;
import com.bartek.supportportal.exception.domain.UsernameExistException;
import com.bartek.supportportal.service.UserService;
import com.bartek.supportportal.utilty.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.bartek.supportportal.constant.FileConstant.*;
import static com.bartek.supportportal.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = {"/user"})
public class UserResource extends ExceptionHandling {

    public static final String NEW_PASSWORD_WAS_SENT_TO = "Email with new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/add")
    public ResponseEntity<User> add(@NotBlank @RequestParam("firstName") String firstName,
                                    @NotBlank @RequestParam("lastName") String lastName,
                                    @NotBlank @RequestParam("username") String username,
                                    @NotBlank @Email @RequestParam("email") String email,
                                    @NotBlank @RequestParam("role") String role,
                                    @NotBlank @RequestParam("isActive") String isActive,
                                    @NotBlank @RequestParam("nonLocked") String nonLocked,
                                    @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

        User newUser = userService.addNewUser(firstName, lastName, username, email, role,
                Boolean.parseBoolean(nonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<User> update(@NotBlank @RequestParam("currentUsername") String currentUsername,
                                       @NotBlank @RequestParam("firstName") String firstName,
                                       @NotBlank @RequestParam("lastName") String lastName,
                                       @NotBlank @RequestParam("username") String username,
                                       @NotBlank @Email @RequestParam("email") String email,
                                       @NotBlank @RequestParam("role") String role,
                                       @NotBlank @RequestParam("isActive") String isActive,
                                       @NotBlank @RequestParam("nonLocked") String nonLocked,
                                       @RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

        User updateUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role,
                Boolean.parseBoolean(nonLocked), Boolean.parseBoolean(isActive), profileImage);

        return new ResponseEntity<>(updateUser, HttpStatus.CREATED);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage(
            @NotBlank @RequestParam("username") String username,
            @RequestParam(value = "profileImage", required = true) MultipartFile profileImage)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> findUser(@NotBlank @PathVariable("username") String username) {
        User foundUser = userService.findUserByUsername(username);
        return new ResponseEntity<>(foundUser, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> userList() {
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@NotBlank @Email @PathVariable("email") String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK, NEW_PASSWORD_WAS_SENT_TO + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@NotBlank @PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @GetMapping(path = "/image/{username}/{filename}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@NotBlank @PathVariable("username") String username, @NotBlank @PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }

    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@NotBlank @PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            int bytesRead;
            byte[] chunk = new byte[1024];
            while ((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead);
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase()), httpStatus);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, tokenProvider.generateToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
