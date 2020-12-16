package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.PCEUploadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadRequestsRepository extends JpaRepository<PCEUploadRequest, Long> {


    List<PCEUploadRequest> findByPayloadHashAndServiceId(String payloadHash, int serviceId);

    List<PCEUploadRequest> findByTinkId(String tinkId);

}
