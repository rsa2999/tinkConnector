package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUserAccountsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TinkUserAccountsRepository extends JpaRepository<TinkUserAccounts, TinkUserAccountsId> {


}
