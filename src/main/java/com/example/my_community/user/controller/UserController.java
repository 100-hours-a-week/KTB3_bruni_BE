package com.example.my_community.user.controller;

import com.example.my_community.user.domain.Role;
import com.example.my_community.user.domain.User;
import com.example.my_community.user.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원 가입 매핑
    @PostMapping(value = "/api/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> signup(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("nickname") String nickname,
            @RequestPart(value = "profileImage", required = false)MultipartFile profileImage
            ) {

        // 프로필 이미지가 있으면 byte[]로 저장
        try {
            byte[] profileImageToByte = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImageToByte = profileImage.getBytes();
            }
            User saved = userService.create(email, password, nickname, profileImageToByte);
            UserResponse res = UserResponse.of(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (IOException e) {
            throw new FileInputException("프로필 이미지를 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    @GetMapping(("/{id}"))
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.of(userService.findById(id));
    }

    @PatchMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.update(id, request.nickname);
        return UserResponse.of(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @Data
    public static class UpdateUserRequest {
        private String nickname;
    }

    @Data
    public static class CreateUserRequest {
        private String email;
        private String password;
        private Role role;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String email;
        private Role role;

        public static UserResponse of(User user) {
            return new UserResponse(user.getId(), user.getEmail(), user.getRole());
        }

        public UserResponse(Long id, String email, Role role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }
    }
}
