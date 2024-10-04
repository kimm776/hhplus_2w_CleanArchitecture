package com.hhplus.CleanArchitecture.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureApplyRequest {

    private Long userId;
    private Long lectureId;

}
