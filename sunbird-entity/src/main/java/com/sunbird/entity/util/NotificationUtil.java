package com.sunbird.entity.util;

import com.sunbird.entity.model.EmailConfig;
import com.sunbird.entity.model.Notification;
import com.sunbird.entity.model.Template;
import com.sunbird.entity.model.TemplateParams;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(Constants.ServiceRepositories.NOTIFICATION_UTIL)
public class NotificationUtil {
    public static final org.slf4j.Logger Logger = LoggerFactory.getLogger(NotificationUtil.class);
    private static final String EXCEPTION = "Exception in %s: %s";

    @Autowired
    ServerProperties appProperties;

    public void sendNotification(TemplateParams params) {
        new Thread(() -> {
            try {
                if (Boolean.valueOf(appProperties.getSendNotification())
                        && !StringUtils.isBlank(params.getAuthToken())) {
                    HttpHeaders headers = new HttpHeaders();
                    RestTemplate restTemplate = new RestTemplate();
                    List<String> sendTo = params.getSendTo();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set(Constants.Parameters.AUTHORIZATION, appProperties.getAuthAPIKey());
                    headers.set(Constants.Parameters.X_USER_TOKEN, params.getAuthToken());
                    params.setSendTo(null);

                    Map<String, Object> notificationRequest = new HashMap<>();
                    List<Object> notificationTosend = new ArrayList<>(Arrays.asList(new Notification("email", "message",
                            new EmailConfig("pritha.chattopadhyay@tarento.com", params.getSubject()), sendTo,
                            new Template(null, "node-rejected", params))));
                    notificationRequest.put("request", new HashMap<String, List<Object>>() {
                        {
                            put("notifications", notificationTosend);
                        }
                    });
                    HttpEntity<Object> req = new HttpEntity<>(notificationRequest, headers);
                    restTemplate.postForEntity(appProperties.getNotificationUrl(), req, String.class);
                }
            } catch (Exception e) {
                Logger.error(String.format(EXCEPTION, "sendNotification", e.getMessage()));
            }
        }).start();
    }
}