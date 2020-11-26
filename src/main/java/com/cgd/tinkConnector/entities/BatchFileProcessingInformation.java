package com.cgd.tinkConnector.entities;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "processedbatchfiles")
public class BatchFileProcessingInformation {

    @Id
    private String fileName;
    private Date processingDate;
    private int status;

}
