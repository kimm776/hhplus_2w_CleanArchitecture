package com.hhplus.CleanArchitecture.application.service;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.LectureHistory;
import com.hhplus.CleanArchitecture.application.entity.Speaker;
import com.hhplus.CleanArchitecture.application.entity.User;
import com.hhplus.CleanArchitecture.application.exception.AlreadyAppliedException;
import com.hhplus.CleanArchitecture.application.exception.LectureCapacityExceededException;
import com.hhplus.CleanArchitecture.domain.repository.LectureHistoryRepository;
import com.hhplus.CleanArchitecture.domain.repository.LectureRepository;
import com.hhplus.CleanArchitecture.domain.repository.SpeakerRepository;
import com.hhplus.CleanArchitecture.domain.repository.UserRepository;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class LectureServiceIntegrationTest {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private SpeakerRepository speakerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureHistoryRepository lectureHistoryRepository;

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 특강 정원 초과")
    public void testApplyLecture_LectureIsFull() {
        // Given
        User user = new User(1L,"Test 유저");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Speaker speaker = new Speaker(1L, "Test 강사");
        speakerRepository.saveSpeaker(speaker);

        Lecture lecture = new Lecture(1L, "Test 특강", speaker, LocalDateTime.now().plusDays(1), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        for (int i = 1; i <= 30; i++) {
            User tempUser = new User((long)i + 1, "User" + i);
            userRepository.saveUser(tempUser);

            LectureHistory lectureHistory = new LectureHistory(tempUser, lecture, LocalDateTime.parse("2024-04-10T13:00:00"), true);

            lectureHistoryRepository.saveLectureHistoryWithLock(lectureHistory);
        }

        LectureApplyRequest request = new LectureApplyRequest();
        request.setUserId(userId);
        request.setLectureId(lectureId);

        // When & Then
        assertThrows(LectureCapacityExceededException.class, () -> {
            lectureService.applyLecture(request);
        });

    }


    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 동일한 유저가 같은 특강을 여러 번 신청")
    public void testApplyLecture_SameUserMultipleApplications() {
        // Given
        User user = new User(1L, "Test 유저");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Speaker speaker = new Speaker(1L, "Test 강사");
        speakerRepository.saveSpeaker(speaker);

        Lecture lecture = new Lecture(1L, "Test 특강", speaker, LocalDateTime.now().plusDays(1), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        LectureApplyRequest request = new LectureApplyRequest();
        request.setUserId(userId);
        request.setLectureId(lectureId);

        // When
        lectureService.applyLecture(request);

        // Then
        assertThrows(AlreadyAppliedException.class, () -> {
            lectureService.applyLecture(request);
        });

        assertThrows(AlreadyAppliedException.class, () -> {
            lectureService.applyLecture(request);
        });

        assertThrows(AlreadyAppliedException.class, () -> {
            lectureService.applyLecture(request);
        });

        assertThrows(AlreadyAppliedException.class, () -> {
            lectureService.applyLecture(request);
        });
    }

}
