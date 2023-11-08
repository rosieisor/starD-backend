package com.web.stard.repository;

import com.web.stard.domain.LoginTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<LoginTestEntity, String> {
}
