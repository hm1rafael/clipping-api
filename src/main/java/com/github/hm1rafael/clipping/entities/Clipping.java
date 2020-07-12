package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.time.LocalDate;

@Getter
@Entity
@EqualsAndHashCode
@NoArgsConstructor
public class Clipping {
    @Id
    private Long id;
    private String clippingMatter;
    private LocalDate clippingDate;
    private ClassificationType classificationType;
    private boolean confirmed;
    @Reference
    @JsonIgnore
    private Alert alert;
    @Reference
    @JsonIgnore
    private HearingAppointment hearingAppointment;

    public Clipping(ClippingRequest clippingRequest) {
        this.clippingMatter = clippingRequest.getClippingMatter();
        this.clippingDate = clippingRequest.getClippingDate();
        this.classificationType = clippingRequest.getClassificationType();
        this.alert = buildAlertIfNeeded(clippingRequest);
        this.hearingAppointment = buildHearingIfNeeded(clippingRequest);
    }

    private HearingAppointment buildHearingIfNeeded(ClippingRequest clippingRequest) {
        if (clippingRequest.isHearing()) {
            return new HearingAppointment(clippingRequest);
        }
        return null;
    }

    private Alert buildAlertIfNeeded(ClippingRequest clippingRequest) {
        if (clippingRequest.isImportant()) {
            return new Alert();
        }
        return null;
    }

    public void confirm() {
        confirmed = Boolean.TRUE;
    }
}
