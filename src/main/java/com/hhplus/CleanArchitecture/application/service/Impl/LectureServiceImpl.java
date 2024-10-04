package com.hhplus.CleanArchitecture.application.service.Impl;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.LectureHistory;
import com.hhplus.CleanArchitecture.application.entity.User;
import com.hhplus.CleanArchitecture.application.exception.*;
import com.hhplus.CleanArchitecture.application.service.LectureService;
import com.hhplus.CleanArchitecture.domain.repository.LectureHistoryRepository;
import com.hhplus.CleanArchitecture.domain.repository.LectureRepository;
import com.hhplus.CleanArchitecture.domain.repository.UserRepository;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyRequest;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    /**
     * 강의 신청
     */
    @Override
    @Transactional
    public void applyLecture(LectureApplyRequest request) {
        //1. 사용자 확인
        User user = userRepository.getUserById(request.getUserId());

        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        //2. 열려있는 강의 정보 조회
        Lecture lecture = lectureRepository.getLectureById(request.getLectureId());
        if (lecture == null) {
            throw new LectureNotFoundException("강의 목록이 없습니다.");
        }
        if(!lecture.isLectureDate(LocalDateTime.now())) {
            throw new LectureNotOpenException("현재 신청 가능한 강의가 아닙니다.");
        }

        //3. 수강내역 확인
        Optional<LectureHistory> lectureHistoryOpt = lectureHistoryRepository.getLectureHistoryWithLock(user, lecture);
        if (lectureHistoryOpt.isPresent()) {
            throw new AlreadyAppliedException("이미 해당 강의를 신청하셨습니다.");
        }

        //4. 잔여 좌석 확인
        long appliedLectureCount = lectureHistoryRepository.getAppliedLectureCount(lecture);
        if(lecture.isFull(appliedLectureCount)) {
            throw new LectureCapacityExceededException("정원이 초과되었습니다.");
        }

        //5. 수강 신청 & 히스토리 저장
        LectureHistory lectureHistory = LectureHistory.apply(user, lecture);
        lectureHistoryRepository.saveLectureHistoryWithLock(lectureHistory);

    }

    //특강 목록 조회 API
    @Override
    public List<Lecture> getLectureList() {

        List<Lecture> lectures = lectureRepository.getLectures();

        if (lectures.isEmpty()) {
            throw new LectureNotFoundException("강의 목록이 없습니다.");
        }

        //오늘 날짜 이후의 강의만 조회 가능
        LocalDateTime now = LocalDateTime.now();
        List<Lecture> lectureList = lectures.stream()
                .filter(lecture -> lecture.getLectureDate().isAfter(now))
                .collect(Collectors.toList());

        return lectureList;

    }

    //특강 신청 완료 목록 조회 API
    @Override
    public List<LectureApplyResponse> getAppliedLectureList(Long userId, Long lectureId) {

        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 사용자입니다.");
        }

        Lecture lecture = lectureRepository.getLectureById(lectureId);
        Optional<LectureHistory> lectureHistoryOpt = lectureHistoryRepository.getLectureHistoryWithLock(user, lecture);

        if (lectureHistoryOpt == null) {
            throw new LectureNotAppliedException("신청한 강의 내역이 없습니다.");
        }

        if (lectureHistoryOpt.isPresent()) {
            LectureHistory lectureHistory = lectureHistoryOpt.get();
            LectureApplyResponse response = new LectureApplyResponse(
                    lectureHistory.getLecture().getId(),
                    lectureHistory.getLecture().getTitle(),
                    lectureHistory.getLecture().getSpeaker().getName(),
                    user.getName()
            );
            return Collections.singletonList(response);

        }else {
            return Collections.emptyList();
        }
    }

}
