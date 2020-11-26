package com.cgd.tinkConnector.Repositories;


import com.cgd.tinkConnector.entities.BatchFileProcessingInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchFilesRepository extends JpaRepository<BatchFileProcessingInformation, String> {
}
