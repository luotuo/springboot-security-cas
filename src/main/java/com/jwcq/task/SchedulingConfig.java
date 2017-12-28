package com.jwcq.task;

import com.jwcq.custom.UserInfo;
import com.jwcq.entity.ResourceLock;

import com.jwcq.entity.UserOnline;
import com.jwcq.security.SecurityConfig;
import com.jwcq.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luotuo on 17-6-2.
 */
@Configuration
@EnableScheduling
@Component
public class SchedulingConfig {
    @Autowired
    private SecurityConfig securityConfig;
    //Check Resource locks every ten minutes
    @Scheduled(cron = "0 0/10 * * * ?")  //秒 分 时
    public void checkResourceLock() {
        System.out.println("SchedulingConfig");
        ResourceLock.checkForAlive();
    }

    // Check user sessions
    @Scheduled(cron = "0 0/10 * * * ?")  //秒 分 时
    public void releaseUserSession() {
        SessionRegistry sessionRegistry = securityConfig.sessionRegistry();
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        List<String> expiredSessionIds = new ArrayList<>();
        for (int j = 0; j < allPrincipals.size(); j++) {
            UserInfo customUser = (UserInfo) allPrincipals.get(j);
            List<SessionInformation> allSessions = sessionRegistry.getAllSessions(customUser, false);
            if (allSessions != null && !allSessions.isEmpty()) {
                for (int i = 0; i < allSessions.size(); i++) {
                    SessionInformation sessionInformation = allSessions.get(i);
                    if (sessionInformation.isExpired()) {
                        sessionInformation.expireNow();
                        expiredSessionIds.add(sessionInformation.getSessionId());
                    }
                }
            }
        }
        for (String s : expiredSessionIds) {
            sessionRegistry.removeSessionInformation(s);
        }
    }

}
