package org.kakaopay.coffee.api.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.user.request.UserLoginServiceRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointServiceRequest;
import org.kakaopay.coffee.api.user.request.UserSignUpServiceRequest;
import org.kakaopay.coffee.api.user.response.UserLoginResponse;
import org.kakaopay.coffee.api.user.response.UserRechargePointResponse;
import org.kakaopay.coffee.db.user.UserEntity;
import org.kakaopay.coffee.db.user.UserJpaManager;
import org.kakaopay.coffee.db.user.UserJpaReader;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryEntity;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaManager;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserJpaReader userJpaReader;
    private final UserJpaManager userJpaManager;

    private final UserPointHistoryJpaReader userPointHistoryJpaReader;
    private final UserPointHistoryJpaManager userPointHistoryJpaManager;


    public UserRechargePointResponse rechargeUserPoint(
        UserRechargePointServiceRequest request) throws Exception {

        Optional<UserEntity> user = userJpaReader.findById(request.getUserId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        userPointHistoryJpaManager.saveAndFlush(UserPointHistoryEntity.of(request));
        long updatedRow = userJpaManager.increasePoint(user.get().getId(),
            user.get().getPoint() + request.getPoint());

        if (updatedRow < 1) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        Optional<UserEntity> resultUser = userJpaReader.findById(request.getUserId());

        if (resultUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return UserRechargePointResponse.of(resultUser.get());
    }

    @Transactional
    public void signUp (
        UserSignUpServiceRequest request) throws Exception {

        if (userJpaReader.countByPhone(request.getPhone()) > 0) {
            throw new IllegalArgumentException("이미 가입한 전화번호 입니다.");
        }

        userJpaManager.save(UserEntity.of(request));
    }

    @Transactional
    public UserLoginResponse login(
        UserLoginServiceRequest request) throws Exception {

        Optional<UserEntity> user = userJpaReader.findByPhoneAndPassword(request.getPhone(),
            request.getPassword());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("전화번호와 비밀번호를 다시 확인해주세요.");
        }

        return UserLoginResponse.of(user.get());
    }


}
