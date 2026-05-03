package com.gosmart.backoffice.dto;

public final class SosPushAlertView {
    private final Integer id;
    private final Integer sosAlertId;
    private final Integer type;
    private final Long pushReceiverId;
    private final String pushReceiverName;
    private final Integer count;
    private final String sentDateTime;
    private final String sentBy;
    private final String ackDateTime;
    private final String timeTaken;
    private final String remark;
    private final String status;

    public SosPushAlertView(
            Integer id,
            Integer sosAlertId,
            Integer type,
            Long pushReceiverId,
            String pushReceiverName,
            Integer count,
            String sentDateTime,
            String sentBy,
            String ackDateTime,
            String timeTaken,
            String remark,
            String status
    ) {
        this.id = id;
        this.sosAlertId = sosAlertId;
        this.type = type;
        this.pushReceiverId = pushReceiverId;
        this.pushReceiverName = pushReceiverName;
        this.count = count;
        this.sentDateTime = sentDateTime;
        this.sentBy = sentBy;
        this.ackDateTime = ackDateTime;
        this.timeTaken = timeTaken;
        this.remark = remark;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSosAlertId() {
        return sosAlertId;
    }

    public Integer getType() {
        return type;
    }

    public Long getPushReceiverId() {
        return pushReceiverId;
    }

    public String getPushReceiverName() {
        return pushReceiverName;
    }

    public Integer getCount() {
        return count;
    }

    public String getSentDateTime() {
        return sentDateTime;
    }

    public String getSentBy() {
        return sentBy;
    }

    public String getAckDateTime() {
        return ackDateTime;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }
}
