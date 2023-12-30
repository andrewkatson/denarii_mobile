package com.denarii.android.user;

import java.io.Serializable;

public class DenariiUser implements Serializable {
    private Integer resetID;
    private String reportID;
    private String verificationReportStatus;
    private boolean isVerified;

    public void setResetID(Integer newResetID) {
        resetID = newResetID;
    }

    public void setReportID(String newReportID) {
        reportID = newReportID;
    }

    public void setVerificationReportStatus(String newStatus) {
        verificationReportStatus = newStatus;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Integer getResetID() {
        return resetID;
    }

    public String getReportID() {
        return reportID;
    }

    public String getVerificationReportStatus() {
        return verificationReportStatus;
    }

    public boolean getIsVerified() {
        return isVerified;
    }
}
