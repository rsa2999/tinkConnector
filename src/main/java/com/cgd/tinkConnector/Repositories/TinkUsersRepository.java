package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TinkUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinkUsersRepository extends JpaRepository<TinkUsers, String> {


}
