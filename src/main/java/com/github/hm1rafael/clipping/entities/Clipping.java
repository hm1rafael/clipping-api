package com.github.hm1rafael.clipping.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.util.Pair;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@Entity
@JsonDeserialize(builder = Clipping.ClippingBuilder.class)
public class Clipping {
    @Id
    private Long id;
    @NotNull
    private String clippingMatter;
    private ClassificationType classificationType;
    @NotNull
    private LocalDate clippingDate;
    private LocalDate classifiedDate;
    private String classifiedTime;
    private boolean important;
    @Setter(AccessLevel.NONE)
    private boolean confirmation;

    public boolean isHearing() {
        return classificationType == ClassificationType.HEARING;
    }

    @JsonPOJOBuilder(withPrefix = StringUtils.EMPTY)
    public static class ClippingBuilder {
        private void populateClassifiedDateAndTime(Pair<LocalDate, String> pair) {
            classifiedDate = pair.getFirst();
            classifiedTime = pair.getSecond();
        }

        public Clipping build() {
            if (Objects.isNull(classifiedDate)) {
                ClassifiedDateExtractor.extract(clippingMatter).ifPresentOrElse(
                        this::populateClassifiedDateAndTime,
                        () -> classifiedDate = HearingDateCalculator.calculateNextBusinessDate(clippingDate));
            }
            return new Clipping(id, clippingMatter, classificationType, clippingDate, classifiedDate, classifiedTime, important, confirmation);
        }
    }

    public void confirm() {
        confirmation = Boolean.TRUE;
    }
}
