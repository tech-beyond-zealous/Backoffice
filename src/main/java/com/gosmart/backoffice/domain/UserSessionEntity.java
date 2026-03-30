package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_session")
public class UserSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "last_activity_dt")
    private LocalDateTime lastActivityDt;

    @Column(name = "expire_dt")
    private LocalDateTime expireDt;

    @Column(name = "revoke_dt")
    private LocalDateTime revokeDt;

    @Column(name = "revoke_reason")
    private String revokeReason;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public LocalDateTime getLastActivityDt() {
        return lastActivityDt;
    }

    public void setLastActivityDt(LocalDateTime lastActivityDt) {
        this.lastActivityDt = lastActivityDt;
    }

    public LocalDateTime getExpireDt() {
        return expireDt;
    }

    public void setExpireDt(LocalDateTime expireDt) {
        this.expireDt = expireDt;
    }

    public LocalDateTime getRevokeDt() {
        return revokeDt;
    }

    public void setRevokeDt(LocalDateTime revokeDt) {
        this.revokeDt = revokeDt;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
