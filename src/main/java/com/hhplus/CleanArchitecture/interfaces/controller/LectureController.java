package com.hhplus.CleanArchitecture.interfaces.controller;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.service.LectureService;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyRequest;
import com.hhplus.CleanArchitecture.interfaces.dto.LectureApplyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    /**
     * TODO - 특강 신청 API
     */
    @PostMapping("/apply")
    public ResponseEntity<Object> lectureApply(@RequestBody LectureApplyRequest request) {
        lectureService.applyLecture(request);
        return ResponseEntity.ok().body("Lecture applied");
    }
    /**
     * TODO - 특강 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<List<Lecture>> getLectureList() {
        List<Lecture> lectureList = lectureService.getLectureList();
        return ResponseEntity.ok(lectureList);
    }

    /**
     * TODO - 특강 신청 완료 목록 조회 API
     */
    @GetMapping("/applied/{userId}")
    public ResponseEntity<List<LectureApplyResponse>> getAppliedLectureList(@PathVariable Long userId, Long lectureId) {
        List<LectureApplyResponse> appliedLectures = lectureService.getAppliedLectureList(userId, lectureId);
        return ResponseEntity.ok(appliedLectures);
    }

}
