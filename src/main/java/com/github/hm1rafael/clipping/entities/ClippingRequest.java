package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ToString
@Entity
public class ClippingRequest {
    @NotNull
    private String clippingMatter;
    private ClassificationType classificationType;
    @NotNull
    private LocalDate clippingDate;
    private LocalDate classifiedDate;
    private String classifiedTime;
    private boolean important;

    @JsonIgnore
    public boolean isHearing() {
        return classificationType == ClassificationType.HEARING;
    }

}
