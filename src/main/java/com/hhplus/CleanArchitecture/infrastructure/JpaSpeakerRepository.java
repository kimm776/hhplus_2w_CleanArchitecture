package com.hhplus.CleanArchitecture.infrastructure;

import com.hhplus.CleanArchitecture.application.entity.Speaker;
import com.hhplus.CleanArchitecture.domain.repository.SpeakerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSpeakerRepository extends JpaRepository<Speaker, Long>, SpeakerRepository {

    @Override
    default Speaker saveSpeaker(Speaker speaker) {
        return save(speaker);
    }

}