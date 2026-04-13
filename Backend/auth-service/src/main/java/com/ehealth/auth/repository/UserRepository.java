package com.ehealth.auth.repository;

import com.ehealth.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    boolean existsByEmail(String email);
}
