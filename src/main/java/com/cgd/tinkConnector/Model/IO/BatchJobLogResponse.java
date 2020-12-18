package com.cgd.tinkConnector.Model.IO;

import com.cgd.tinkConnector.entities.BatchFile;

import java.util.List;

public class BatchJobLogResponse {

    private List<BatchFile> processedJobs;

    public List<BatchFile> getProcessedJobs() {
        return processedJobs;
    }

    public void setProcessedJobs(List<BatchFile> processedJobs) {
        this.processedJobs = processedJobs;
    }
}
