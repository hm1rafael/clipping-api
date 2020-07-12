package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.datastore.Key;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HearingAppointment {
    @Id
    @JsonIgnore
    private Key hearingId;
    private LocalDate classifiedDate;
    private String classifiedTime;

    @JsonProperty
    Long getHearingId() {
        return Optional.ofNullable(hearingId).map(Key::getId).orElse(0L);
    }

    public HearingAppointment(ClippingRequest clippingRequest) {
        this.classifiedDate = clippingRequest.getClassifiedDate();
        if (Objects.isNull(this.classifiedDate)) {
            ClassifiedDateExtractor.extract(clippingRequest.getClippingMatter())
                    .ifPresentOrElse(
                            this::populateClassifiedDateAndTime,
                            () -> this.classifiedDate = HearingDateCalculator.calculateNextBusinessDate(clippingRequest.getClippingDate()));
        }
        this.classifiedTime = clippingRequest.getClassifiedTime();
    }

    private void populateClassifiedDateAndTime(Pair<LocalDate, String> pair) {
        classifiedDate = pair.getFirst();
        classifiedTime = pair.getSecond();
    }

}
