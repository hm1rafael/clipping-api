package com.github.hm1rafael.clipping.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HearingAppointment {
    @Id
    private Long hearingId;
    private LocalDate classifiedDate;
    private String classifiedTime;

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
