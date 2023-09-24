package com.ssafy.enjoytrip.user.model.entity;

import com.ssafy.enjoytrip.global.error.UserException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User {

    private String userId;
    private String name;
    private String address;
    private String password;
    private String email;
    private int authority;
    private String salt;

    @Builder
    public User(
        final String userId, final String name,
        final String address, final String password,
        final String email, final int authority,
        final String salt
    ) {
        validateId(userId);
        validateName(name);
        validateAddress(address);
        validatePassword(password);
        validateEmail(email);
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.password = password;
        this.email = email;
        this.authority = authority;
        this.salt = salt;
    }

    private void validateId(final String userId) {
        if (userId == null || userId.isBlank()) {
            throw new UserException("유저의 아이디는 필수 값입니다.");
        }
    }

    private void validateEmail(final String email) {
        if (email == null || email.isBlank()) {
            throw new UserException("유저의 이메일은 필수 값입니다.");
        }
    }

    private void validatePassword(final String password) {
        if (password == null || password.isBlank()) {
            throw new UserException("유저의 비밀번호는 필수 값입니다.");
        }
    }

    private void validateAddress(final String address) {
        if (address == null || address.isBlank()) {
            throw new UserException("유저의 주소는 필수 값입니다.");
        }
    }

    private void validateName(final String name) {
        if (name == null || name.isBlank()) {
            throw new UserException("유저의 이름은 필수 값입니다.");
        }
    }
}