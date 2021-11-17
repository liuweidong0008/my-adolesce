package com.adolesce.cloud.db.repository;

import com.adolesce.cloud.dubbo.domain.db.JpaAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAddressRepository extends JpaRepository<JpaAddress, Long> {

}
