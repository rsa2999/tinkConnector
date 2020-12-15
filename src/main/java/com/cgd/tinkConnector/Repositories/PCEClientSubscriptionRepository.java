package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.PCEClientSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PCEClientSubscriptionRepository extends JpaRepository<PCEClientSubscription, String> {
}
