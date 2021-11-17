package com.adolesce.cloud.db.repository;

import com.adolesce.cloud.dubbo.domain.db.JpaIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIdentityRepository extends JpaRepository<JpaIdentity, Long> {

}
