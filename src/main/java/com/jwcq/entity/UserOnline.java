package com.jwcq.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by luotuo on 17-6-5.
 */
@Data
public class UserOnline {
    private Long userId;
    private String userName;
    private String ip;
    private String sessionId;
    private Date lastUpdateTime;
    private Date loginTime;
    private String token;
}
