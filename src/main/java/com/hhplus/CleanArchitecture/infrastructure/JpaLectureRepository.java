package com.hhplus.CleanArchitecture.infrastructure;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.domain.repository.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaLectureRepository extends JpaRepository<Lecture, Long>, LectureRepository {

    @Override
    default List<Lecture> getLectures() {
        return findAll();
    }

    @Override
    default Lecture saveLecture(Lecture lecture) {
        return save(lecture);
    }

}
