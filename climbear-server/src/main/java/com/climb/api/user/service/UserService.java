package com.climb.api.user.service;

import com.climb.api.auth.service.RefreshTokenService;
import com.climb.api.user.domain.dto.request.SetUserInfoRequestDTO;
import com.climb.api.user.domain.dto.response.UserExpResponseDTO;
import com.climb.api.user.domain.dto.response.UserInfoResponseDTO;
import com.climb.api.user.domain.entity.User;
import com.climb.api.user.domain.type.ClimbingLevel;
import com.climb.api.user.repository.UserRepository;
import com.climb.common.exception.BusinessException;
import com.climb.common.exception.ErrorCode;
import com.climb.common.response.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public String getEmailByUserId(Integer userId) {
        User user = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }

    public UserInfoResponseDTO getUserInfo(Integer userId) {
        User user = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserInfoResponseDTO.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .height(user.getHeight())
                .armSpan(user.getArmSpan())
                .exp(user.getExp())
                .build();
    }
    @Transactional
    public CustomApiResponse<String> setUserInfo(Integer userId, SetUserInfoRequestDTO request) {
        User user = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(request.height() != null) {
            if(request.height() < 120) {
                return CustomApiResponse.error(400, "키는 120cm 이상이어야 합니다");
            }
            user.updateHeight(request.height());
        }

        if(request.armSpan() != null) {
            if(request.armSpan() < 100) {
                return CustomApiResponse.error(400, "팔 길이는 100cm 이상이어야 합니다");
            }
            user.updateArmSpan(request.armSpan());
        }

        if(request.height() != null || request.armSpan() != null) {
            userRepository.save(user);
        }

        return CustomApiResponse.success("회원 정보 수정을 성공했습니다");
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.deleteRefreshToken(userId);

        // 회원 탈퇴 처리 - 이메일, 닉네임, 카카오 ID 탈퇴 처리 + flag 변경
        user.resignUser();
        userRepository.save(user);
        log.info("사용자 ID {}의 회원 탈퇴 처리가 완료되었습니다.", userId);
    }

    public UserExpResponseDTO getExpUser(Integer userId) {
        User user = userRepository.findByUserIdAndResignFlagFalse(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        ClimbingLevel currentLevel = determineLevel(user.getExp());

        String nextLevelName;
        if (currentLevel.ordinal() < ClimbingLevel.values().length - 1) { // 현재 레벨이 최고 레벨이 아닌 경우
            nextLevelName = ClimbingLevel.values()[currentLevel.ordinal() + 1].getLevelName();
        } else { // 현재 레벨이 최고 레벨인 경우
            nextLevelName = currentLevel.getLevelName();
        }

        return UserExpResponseDTO.builder()
                .exp(user.getExp())
                .maxExp(currentLevel.getMaxExp())
                .levelName(currentLevel.getLevelName())
                .nextLevelName(nextLevelName)
                .build();
    }

    private ClimbingLevel determineLevel(Long exp) {
        ClimbingLevel[] levels = ClimbingLevel.values();

        // 경험치가 가장 높은 레벨의 최대 경험치보다 많으면 가장 높은 레벨 반환
        if (exp >= levels[levels.length - 1].getMaxExp()) {
            return levels[levels.length - 1];
        }

        // 경험치에 맞는 레벨 찾기
        for (int i = 0; i < levels.length; i++) {
            if (exp < levels[i].getMaxExp()) {
                return levels[i];
            }
        }

        return levels[0];
    }
}
