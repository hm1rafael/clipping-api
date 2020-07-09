package com.github.hm1rafael.clipping.entities;

import lombok.Builder;
import lombok.Getter;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;

import java.time.LocalDate;

@Entity
@Getter
@Builder
public class HearingAppointment {
    @Id
    private Long id;
    private LocalDate classifiedDate;
    @Reference
    private Clipping clipping;
}
