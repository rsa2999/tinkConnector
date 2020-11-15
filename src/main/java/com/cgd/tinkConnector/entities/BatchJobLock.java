package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "jobLock")
public class BatchJobLock {

    private String serverId;
    private Date lastUpload;


}
