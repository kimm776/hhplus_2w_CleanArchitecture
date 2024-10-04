package com.hhplus.CleanArchitecture.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LectureApplyResponse {

    private Long lectureId;
    private String lectureTitle;
    private String speakerName;
    private String userName;

}
