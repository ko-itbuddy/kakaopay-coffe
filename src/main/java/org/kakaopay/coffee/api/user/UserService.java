package org.kakaopay.coffee.api.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.user.request.UserLoginServiceRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointServiceRequest;
import org.kakaopay.coffee.api.user.request.UserSignUpServiceRequest;
import org.kakaopay.coffee.api.user.response.UserLoginResponse;
import org.kakaopay.coffee.api.user.response.UserRechargePointResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    private final UserPointHistoryRepository userPointHistoryRepository;

    @Transactional
    public UserRechargePointResponse rechargeUserPoint(
        UserRechargePointServiceRequest request) throws Exception {

        Optional<UserEntity> user = userRepository.findById(request.getUserId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        userPointHistoryRepository.saveAndFlush(UserPointHistoryEntity.of(request));

        user = userRepository.findById(request.getUserId());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        return UserRechargePointResponse.of(user.get());
    }

    @Transactional
    public void signUp (
        UserSignUpServiceRequest request) throws Exception {

        if (userRepository.countByPhone(request.getPhone()) > 0) {
            throw new IllegalArgumentException("이미 가입한 전화번호 입니다.");
        }

        userRepository.save(UserEntity.of(request));

    }

    @Transactional
    public UserLoginResponse login(
        UserLoginServiceRequest request) throws Exception {

        Optional<UserEntity> user = userRepository.findByPhoneAndPassword(request.getPhone(),
            request.getPassword());

        if (user.isEmpty()) {
            throw new IllegalArgumentException("전화번호와 비밀번호를 다시 확인해주세요.");
        }

        return UserLoginResponse.of(user.get());
    }


}
