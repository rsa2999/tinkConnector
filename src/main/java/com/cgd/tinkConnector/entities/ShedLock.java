package com.cgd.tinkConnector.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "shedLock")
public class ShedLock {


    @Id
    private String name;

    @Column(name = "lock_until")
    private Date lockUntil;

    @Column(name = "locked_at")
    private Date lockedAt;

    @Column(name = "locked_by")
    private String lockedbY;


}
