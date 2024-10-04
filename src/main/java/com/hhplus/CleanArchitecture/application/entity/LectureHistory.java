package com.hhplus.CleanArchitecture.application.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LectureHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalDateTime appliedDate;

    private Boolean isApplied;

    public LectureHistory(User user, Lecture lecture, LocalDateTime appliedDate, Boolean isApplied) {
        this.user = user;
        this.lecture = lecture;
        this.appliedDate = appliedDate;
        this.isApplied = isApplied;
    }

    public static LectureHistory apply(User user, Lecture lecture) {
        LectureHistory lectureHistory = new LectureHistory(user, lecture, LocalDateTime.now(), true);
        return lectureHistory;
    }

}
