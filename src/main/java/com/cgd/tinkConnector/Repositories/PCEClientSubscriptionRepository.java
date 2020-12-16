package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.PCEClientSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PCEClientSubscriptionRepository extends JpaRepository<PCEClientSubscription, String> {

    Optional<PCEClientSubscription> findByNumClient(Long numClient);

    List<PCEClientSubscription> findByNumContrato(Long numClient);

}
