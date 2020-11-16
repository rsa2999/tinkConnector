package com.cgd.tinkConnector.entities;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "jobLock")
public class BatchJobLock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private String serverId;
    private Date lastUpload;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public Date getLastUpload() {
        return lastUpload;
    }

    public void setLastUpload(Date lastUpload) {
        this.lastUpload = lastUpload;
    }
}
