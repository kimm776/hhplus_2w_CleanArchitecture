package com.hhplus.CleanArchitecture.domain.repository;

import com.hhplus.CleanArchitecture.application.entity.Lecture;
import com.hhplus.CleanArchitecture.application.entity.Speaker;

public interface SpeakerRepository {
    Speaker saveSpeaker(Speaker speaker);
}
