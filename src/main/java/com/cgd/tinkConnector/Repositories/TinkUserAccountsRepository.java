package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TinkUserAccountId;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TinkUserAccountsRepository extends JpaRepository<TinkUserAccounts, TinkUserAccountId> {

    List<TinkUserAccounts> findByIdTinkId(String tinkId);

}
