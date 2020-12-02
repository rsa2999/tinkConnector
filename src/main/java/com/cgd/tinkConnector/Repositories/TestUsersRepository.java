package com.cgd.tinkConnector.Repositories;

import com.cgd.tinkConnector.entities.TestUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TestUsersRepository extends JpaRepository<TestUsers, Long> {
}
