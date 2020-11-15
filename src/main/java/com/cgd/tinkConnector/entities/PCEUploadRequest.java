package com.cgd.tinkConnector.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "pceuploadsrequests")
public class PCEUploadRequest {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    private long numClient;
    private String tinkId;
    private String subscriptionId;
    private Date requestDate;
    private int statusCode;

}
