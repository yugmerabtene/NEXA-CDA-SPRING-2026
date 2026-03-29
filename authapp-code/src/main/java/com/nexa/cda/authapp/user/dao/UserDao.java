package com.nexa.cda.authapp.user.dao;

import com.nexa.cda.authapp.user.model.AppUser;
import java.util.Optional;

public interface UserDao {

    boolean existsByEmail(String email);

    Optional<AppUser> findByEmail(String email);

    AppUser save(AppUser user);
}
