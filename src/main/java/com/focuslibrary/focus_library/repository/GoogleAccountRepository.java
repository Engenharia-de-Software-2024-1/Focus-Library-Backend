package com.focuslibrary.focus_library.repository;

import com.focuslibrary.focus_library.model.GoogleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleAccountRepository
extends JpaRepository<GoogleAccount, String> {
    Optional<GoogleAccount> findByGoogleId(String googleId);
}
