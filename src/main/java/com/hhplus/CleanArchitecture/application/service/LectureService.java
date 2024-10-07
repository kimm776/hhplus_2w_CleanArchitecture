package com.hhplus.CleanArchitecture.application.service;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyRequest;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyResponse;

import java.util.List;

public interface LectureService {

    //특강 신청 API
    void applyLecture(LectureApplyRequest request);

    //특강 목록 조회 API
    List<Lecture> getLectureList();

    //특강 신청 완료 목록 조회 API
    List<LectureApplyResponse> getAppliedLectureList(Long userId, Long lectureId);

}
