package com.hhplus.CleanArchitecture.domain.repository;

import com.hhplus.CleanArchitecture.application.entity.Lecture;

import java.util.List;

public interface LectureRepository {

    List<Lecture> getLectures();

    Lecture getLectureById(Long lectureId);

    Lecture saveLecture(Lecture lecture);

}
