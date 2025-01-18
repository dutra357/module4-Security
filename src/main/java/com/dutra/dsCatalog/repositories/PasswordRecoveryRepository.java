package com.dutra.dsCatalog.repositories;

import com.dutra.dsCatalog.entities.PasswordRecover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecover, Long> {
}
