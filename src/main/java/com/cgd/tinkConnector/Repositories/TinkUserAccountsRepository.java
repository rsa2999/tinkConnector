package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TinkUserAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TinkUserAccountsRepository extends JpaRepository<TinkUserAccounts, String> {


}
