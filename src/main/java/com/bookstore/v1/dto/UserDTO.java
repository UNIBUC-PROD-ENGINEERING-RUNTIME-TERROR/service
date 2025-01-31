package com.bookstore.v1.dto;

import com.bookstore.v1.data.User;

import java.util.Objects;

public class UserDTO {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;

    public UserDTO() {}

    public UserDTO(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }

    public UserDTO(String id, String userName, String email, String phoneNumber) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User toUser(Boolean withoutId) {
        User user = new User();
        if (!withoutId) {
            user.setId(id);
        }
        user.setUserName(userName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UserDTO userDTO = (UserDTO) object;
        return id.equals(userDTO.id) &&
               java.util.Objects.equals(userName, userDTO.userName) &&
               java.util.Objects.equals(email, userDTO.email) &&
               java.util.Objects.equals(phoneNumber, userDTO.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userName, email, phoneNumber);
    }

    @Override
    public java.lang.String toString() {
        return "UserDTO{" +
               "id='" + id + '\'' +
               ", userName='" + userName + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               '}';
    }
}