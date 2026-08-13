package com.vallab.store.repositories;

import com.vallab.store.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
