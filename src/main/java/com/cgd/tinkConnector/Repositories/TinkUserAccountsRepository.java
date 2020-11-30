package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TinkUserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinkUserAccountsRepository extends JpaRepository<TinkUserAccounts, String> {

    // Optional<TinkUserAccounts> findByInternalId(String internalId);

    List<TinkUserAccounts> findByTinkId(String tinkId);

}
