package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.PCEUploadRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UploadRequestsRepository extends JpaRepository<PCEUploadRequest, Long> {

    @Transactional
    List<PCEUploadRequest> findByPayloadHashAndServiceId(String payloadHash, int serviceId);

    @Transactional
    List<PCEUploadRequest> findByTinkId(String tinkId);

}
