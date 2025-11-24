package com.example.my_community.user.dto;

import com.example.my_community.user.domain.Role;
import com.example.my_community.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
        private Long id;
        private String email;
        private byte[] profileImage;
        private Role role;

        public static UserResponse of(User user) {
            return new UserResponse(user.getId(), user.getEmail(), user.getProfileImage(), user.getRole());
        }

}
