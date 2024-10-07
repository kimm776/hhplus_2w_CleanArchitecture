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
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaker_id")
    private Speaker speaker;

    private LocalDateTime lectureDate;

    private Integer capacity;

    public boolean isLectureDate(LocalDateTime now) {
        return now.isAfter(lectureDate);
    }

    public boolean isFull(long appliedCount) {
        return appliedCount >= capacity;
    }

}
