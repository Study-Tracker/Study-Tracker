package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.PasswordResetToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByToken(String token);

  List<PasswordResetToken> findByUserId(Long userId);

}
