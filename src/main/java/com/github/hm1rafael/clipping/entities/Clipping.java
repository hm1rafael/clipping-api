package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.*;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Getter
@Builder
@ToString
@Entity
@JsonDeserialize(builder = Clipping.ClippingBuilder.class)
public class Clipping {
    @Id
    private Long id;
    @NotNull
    private String clippingMatter;
    @Builder.Default
    private ClassificationType classificationType = ClassificationType.HEARING;
    @NotNull
    private LocalDate clippingDate;
    private LocalDate classifiedDate;
    private String classifiedTime;
    @Builder.Default
    private boolean important = Boolean.FALSE;
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private boolean confirmation = Boolean.FALSE;

    public boolean isHearing() {
        return classificationType == ClassificationType.HEARING;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ClippingBuilder {
        private static final int DEFAULT_OFFSET = 3;

        public ClippingBuilder classifiedDate(LocalDate classifiedDate) {
            this.classifiedDate = Objects.requireNonNullElseGet(classifiedDate,
                    () -> extractFromClippingMatter().orElseGet(this::getFutureDateFromClippingDate));
            return this;
        }

        private Optional<LocalDate> extractFromClippingMatter() {
            return Optional.empty();
        }

        private LocalDate getFutureDateFromClippingDate() {
            return DateCalculator.calculateNextBusinessDate(clippingDate, DEFAULT_OFFSET);
        }
    }

    public void confirm() {
        confirmation = Boolean.TRUE;
    }

}
