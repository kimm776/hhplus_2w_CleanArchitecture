package com.hhplus.CleanArchitecture.domain.repository;

import com.hhplus.CleanArchitecture.application.entity.User;

public interface UserRepository {

    User getUserById(Long userId);

    User saveUser(User user);

}
