package com.hhplus.CleanArchitecture.infrastructure;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.LectureHistory;
import com.hhplus.CleanArchitecture.application.entity.User;
import com.hhplus.CleanArchitecture.domain.repository.LectureHistoryRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaLectureHistoryRepository extends JpaRepository<LectureHistory, Long>, LectureHistoryRepository {

    @Override
    default Long getAppliedLectureCount(Lecture lecture){
        return countByLectureAndIsAppliedTrue(lecture);
    }
    Long countByLectureAndIsAppliedTrue(Lecture lecture);

    @Override
    default Optional<LectureHistory> getLectureHistoryWithLock(User user, Lecture lecture) {
        return findByUserAndLectureWithLock(user, lecture);
    }

    @Override
    default boolean isAppliedLecture(User user, Lecture lecture){
        return existsByUserAndLectureAndIsAppliedTrue(user, lecture);
    }
    Boolean existsByUserAndLectureAndIsAppliedTrue(User user, Lecture lecture);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default LectureHistory saveLectureHistoryWithLock(LectureHistory lectureHistory) {
        return save(lectureHistory);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lh FROM LectureHistory lh WHERE lh.user = :user AND lh.lecture = :lecture")
    Optional<LectureHistory> findByUserAndLectureWithLock(@Param("user") User user, @Param("lecture") Lecture lecture);

}