package com.otp.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

@Data
@Service
@ConfigurationProperties(prefix = "otp.config")
@RefreshScope
public class Properties {
    /**
     * Expire period in cache
     */
    private int expireMins;

    /**
     * Minimum random number range
     */
    private int minRange;

    /**
     * Maximum random number range
     */
    private int maxRange;

    /**
     * Status after successfully generating/validating otp
     */
    private String successStatus;

    /**
     * Status after failing while generating/validating otp
     */
    private String failedStatus;

    /**
     * Status for an expired otp
     */
    private String expiredStatus;

    /**
     * Encryption key
     */
    private String secretKey;

    /**
     * Salt
     */
    private String salt;

    /**
     * Sms web service
     */
    private String smsWebService;

    /**
     * Sms rpc method
     */
    private String smsMethod;
}
