package com.hhplus.CleanArchitecture.application.service;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.LectureHistory;
import com.hhplus.CleanArchitecture.application.entity.Speaker;
import com.hhplus.CleanArchitecture.application.entity.User;
import com.hhplus.CleanArchitecture.application.exception.AlreadyAppliedException;
import com.hhplus.CleanArchitecture.application.exception.LectureCapacityExceededException;
import com.hhplus.CleanArchitecture.application.exception.LectureNotFoundException;
import com.hhplus.CleanArchitecture.application.exception.UserNotFoundException;
import com.hhplus.CleanArchitecture.application.service.Impl.LectureServiceImpl;
import com.hhplus.CleanArchitecture.domain.repository.LectureHistoryRepository;
import com.hhplus.CleanArchitecture.domain.repository.LectureRepository;
import com.hhplus.CleanArchitecture.domain.repository.UserRepository;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyRequest;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @InjectMocks
    private LectureServiceImpl lectureServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureHistoryRepository lectureHistoryRepository;


    @Test
    @DisplayName("강의 목록 조회 예외 발생 테스트 - 강의 목록이 없음")
    public void LectureNotFoundExceptionTest() {
        //Given
        when(lectureRepository.getLectures()).thenReturn(List.of());

        //When & Then
        LectureNotFoundException exception = assertThrows(LectureNotFoundException.class, () -> {
            lectureServiceImpl.getLectureList();
        });

        verify(lectureRepository, times(1)).getLectures();
        assertThat(exception.getMessage()).isEqualTo("강의 목록이 없습니다.");
    }


    @DisplayName("강의 목록 조회 성공 테스트 - 오늘 날짜 이후의 강의만 조회 가능")
    @Test
    void getLectureListTest() {
        // Given
        Speaker speaker1 = new Speaker(1L, "김강사");
        Speaker speaker2 = new Speaker(2L, "이강사");

        LocalDateTime now = LocalDateTime.now();
        Lecture lecture1 = new Lecture(1L, "내일 특강", speaker1, now.plusDays(1), 30); // 내일 강의
        Lecture lecture2 = new Lecture(2L, "어제 특강", speaker2, now.minusDays(1), 30); // 어제 강의

        List<Lecture> lectureList = List.of(lecture1, lecture2);

        when(lectureRepository.getLectures()).thenReturn(lectureList);

        // When
        List<Lecture> response = lectureServiceImpl.getLectureList();

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(lectureRepository, times(1)).getLectures();
    }


    @Test
    @DisplayName("특강 신청 완료 목록 조회 예외 발생 테스트 - 존재하지 않는 사용자")
    public void UserNotFoundExceptionTest() {
        //Given
        when(userRepository.getUserById(1L)).thenReturn(null);

        //When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            lectureServiceImpl.getAppliedLectureList(1L, 1L);
        });

        verify(userRepository, times(1)).getUserById(1L);
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");

    }


    @Test
    @DisplayName("특강 신청 완료 목록 조회 성공 테스트")
    public void getAppliedLectureListTest() {
        // Given
        User user = new User(1L, "이학생");
        Speaker speaker1 = new Speaker(1L, "김강사");

        LocalDateTime now = LocalDateTime.now();
        Lecture lecture1 = new Lecture(1L, "내일 특강", speaker1, now.plusDays(1), 30);

        LectureHistory lectureHistory = new LectureHistory(user, lecture1, now, true);

        when(userRepository.getUserById(1L)).thenReturn(user);
        when(lectureRepository.getLectureById(1L)).thenReturn(lecture1);
        when(lectureHistoryRepository.getLectureHistoryWithLock(user, lecture1)).thenReturn(Optional.of(lectureHistory));

        // When
        List<LectureApplyResponse> response = lectureServiceImpl.getAppliedLectureList(1L, 1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(lecture1.getId(), response.get(0).getLectureId());
    }


    @Test
    @DisplayName("특강 신청 예외 발생 테스트 - 이미 신청한 강의")
    public void applyLecture_alreadyApplied() {
        // Given
        Long userId = 1L;
        Long lectureId = 1L;

        LectureApplyRequest request = new LectureApplyRequest();
        request.setUserId(userId);
        request.setLectureId(lectureId);

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "내일 특강", new Speaker(1L, "김강사"), LocalDateTime.now().plusDays(1), 30);

        LectureHistory lectureHistory = new LectureHistory(1L, user, lecture, LocalDateTime.now(), true);
        Optional<LectureHistory> optionalLectureHistory = Optional.of(lectureHistory);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.getLectureHistoryWithLock(user, lecture)).thenReturn(optionalLectureHistory);

        // When & Then
        AlreadyAppliedException exception = assertThrows(AlreadyAppliedException.class, () -> {
            lectureServiceImpl.applyLecture(request);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 해당 강의를 신청하셨습니다.");

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).getLectureHistoryWithLock(user, lecture);
    }


    @Test
    @DisplayName("강의 신청 테스트 - 정원 초과")
    public void applyLecture_capacityExceeded() {
        // Given
        Long userId = 1L;
        Long lectureId = 1L;

        LectureApplyRequest request = new LectureApplyRequest();
        request.setUserId(userId);
        request.setLectureId(lectureId);

        User user = new User(userId, "홍길동");
        Lecture lecture = new Lecture(lectureId, "내일 특강", new Speaker(1L, "김강사"), LocalDateTime.now().plusDays(1), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.getAppliedLectureCount(lecture)).thenReturn(30L);

        // When & Then
        LectureCapacityExceededException exception = assertThrows(LectureCapacityExceededException.class, () -> {
            lectureServiceImpl.applyLecture(request);
        });

        assertThat(exception.getMessage()).isEqualTo("정원이 초과되었습니다.");

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).getAppliedLectureCount(lecture);
    }


    @Test
    @DisplayName("특강 신청 정상 작동 테스트")
    public void applyLecture_success() {
        // Given
        Long userId = 1L;
        Long lectureId = 1L;

        LectureApplyRequest request = new LectureApplyRequest();
        request.setUserId(userId);
        request.setLectureId(lectureId);

        User user = new User(userId, "홍길동");
        Lecture lecture = new Lecture(lectureId, "내일 특강", new Speaker(1L, "김강사"), LocalDateTime.now().plusDays(1), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.getLectureHistoryWithLock(user, lecture)).thenReturn(Optional.empty());
        when(lectureHistoryRepository.getAppliedLectureCount(lecture)).thenReturn(0L);

        // When
        lectureServiceImpl.applyLecture(request);

        // Then
        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).getLectureHistoryWithLock(user, lecture);

    }


}