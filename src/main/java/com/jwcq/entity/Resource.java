package com.jwcq.entity;

import com.jwcq.global.EncryptionAlgs;
import com.jwcq.user.entity.User;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Created by luotuo on 17-6-2.
 */
@Data
public class Resource {
    private String id;
    private Timestamp lastUpdateTime;
    private Long userId;
    private String userName;
    private String resourceName;
    private String resourceCode;
    private String sessionId;
    private String taskCode;
    private String name; // Tell the user what it is

    public Resource(String resourceCode,
                    String resourceName,
                    User user,
                    String sessionId,
                    String taskCode,
                    String name) {
        this.id = EncryptionAlgs.getMD5(resourceCode + resourceName);
        this.resourceCode = resourceCode;
        this.userId = user.getId();
        this.userName =user.getName();
        this.resourceName = resourceName;
        this.sessionId = sessionId;
        this.lastUpdateTime = new Timestamp(System.currentTimeMillis());
        this.taskCode = taskCode;
        this.name = name;
    }

    public void updateTimestamp() { lastUpdateTime = new Timestamp(System.currentTimeMillis()); }
}
