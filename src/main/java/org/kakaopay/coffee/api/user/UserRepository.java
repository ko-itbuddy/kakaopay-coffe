package org.kakaopay.coffee.api.user;

import java.util.List;
import java.util.Optional;
import org.kakaopay.coffee.api.menu.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public Long countByPhone(String phone);

    public Optional<UserEntity> findByPhoneAndPassword(String phone, String password);
}
