package com.jwcq.entity;

import com.jwcq.user.entity.User;
import com.jwcq.user.entity.UserResponse;
import lombok.Data;

/**
 * Created by luotuo on 17-11-1.
 */
@Data
public class AllUsers {
    private Long userId;
    private String userCode;
    private String userName;

    public AllUsers(User user) {
        this.userId = user.getId();
        this.userCode = user.getEmail();
        this.userName = user.getName();
    }

    public AllUsers(UserResponse userResponse) {
        this.userId = userResponse.getId();
        this.userName = userResponse.getName();
        this.userCode = userResponse.getEmail();
    }

    public AllUsers() {}
}
