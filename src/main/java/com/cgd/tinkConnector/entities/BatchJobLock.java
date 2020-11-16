package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "jobLock")
public class BatchJobLock {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private String serverId;
    private Date lastUpload;


}
