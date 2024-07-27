package org.kakaopay.coffee.db.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Long countByPhone(String phone);

    public Optional<UserEntity> findByPhoneAndPassword(String phone, String password);
}
