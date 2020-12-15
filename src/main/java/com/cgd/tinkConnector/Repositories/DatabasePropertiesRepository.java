package com.cgd.tinkConnector.Repositories;


import com.cgd.tinkConnector.entities.DatabaseProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabasePropertiesRepository extends JpaRepository<DatabaseProperties, String> {

}
