package com.github.hm1rafael.clipping.events;

import com.github.hm1rafael.clipping.entities.Alert;
import com.github.hm1rafael.clipping.entities.Clipping;
import com.github.hm1rafael.clipping.entities.HearingAppointment;
import com.github.hm1rafael.clipping.repositories.AlertRepository;
import com.github.hm1rafael.clipping.repositories.HearingAppointmentRepository;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class ClippingEventsHandler {

    private final AlertRepository alertRepository;
    private final HearingAppointmentRepository hearingAppointmentRepository;

    public ClippingEventsHandler(AlertRepository alertRepository, HearingAppointmentRepository hearingAppointmentRepository) {
        this.alertRepository = alertRepository;
        this.hearingAppointmentRepository = hearingAppointmentRepository;
    }

    @HandleAfterSave
    public void createAlerts(Clipping clipping) {
        if (!clipping.isImportant()) {
            return;
        }
        Alert alert = Alert.builder()
                .clipping(clipping)
                .build();
        alertRepository.save(alert);
    }

    @HandleAfterSave
    public void createHearings(Clipping clipping) {
        if (!clipping.isHearing()) {
            return;
        }
        HearingAppointment hearingAppointment = HearingAppointment.builder()
                .classifiedDate(clipping.getClassifiedDate())
                .clipping(clipping)
                .build();
        hearingAppointmentRepository.save(hearingAppointment);
    }

}
