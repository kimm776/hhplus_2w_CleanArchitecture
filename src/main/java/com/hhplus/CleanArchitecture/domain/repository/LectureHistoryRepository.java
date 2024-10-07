package com.hhplus.CleanArchitecture.domain.repository;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.LectureHistory;
import com.hhplus.CleanArchitecture.application.entity.User;

import java.util.Optional;

public interface LectureHistoryRepository {

    Long getAppliedLectureCount(Lecture lecture);

    boolean isAppliedLecture(User user, Lecture lecture);

    Optional<LectureHistory> getLectureHistoryWithLock(User user, Lecture lecture);

    LectureHistory saveLectureHistoryWithLock(LectureHistory lectureHistory);

}
