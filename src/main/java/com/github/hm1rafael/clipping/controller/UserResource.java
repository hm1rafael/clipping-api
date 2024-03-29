package com.github.hm1rafael.clipping.controller;

import com.github.hm1rafael.clipping.entities.Alert;
import com.github.hm1rafael.clipping.entities.HearingAppointment;
import com.github.hm1rafael.clipping.repositories.AlertRepository;
import com.github.hm1rafael.clipping.repositories.HearingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private final AlertRepository alertRepository;
    private final HearingRepository hearingRepository;

    public UserResource(AlertRepository alertRepository, HearingRepository hearingRepository) {
        this.alertRepository = alertRepository;
        this.hearingRepository = hearingRepository;
    }

    @GetMapping("/alerts")
    public Page<Alert> alerts(Pageable pageable) {
        return alertRepository.findAll(pageable);
    }

    @GetMapping("/hearings")
    public Page<HearingAppointment> hearings(Pageable pageable) {
        return hearingRepository.findAll(pageable);
    }

}
