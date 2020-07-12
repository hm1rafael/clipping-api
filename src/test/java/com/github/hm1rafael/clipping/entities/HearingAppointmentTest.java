package com.github.hm1rafael.clipping.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class HearingAppointmentTest {

    @Test
    void shouldNotExtractDateFromClippingMatter() {
        ClippingRequest clippingRequest = new ClippingRequest();
        clippingRequest.setClassificationType(ClassificationType.HEARING);
        LocalDate expectedDate = LocalDate.of(2020, 7, 18);
        clippingRequest.setClassifiedDate(expectedDate);
        clippingRequest.setClippingMatter("Conciliação para a data de 20/07/2020 às 10:00h");
        clippingRequest.setClippingDate(LocalDate.of(2020, 7, 20));

        HearingAppointment h = new HearingAppointment(clippingRequest);
        LocalDate actualDate = h.getClassifiedDate();
        assertThat(actualDate).isEqualTo(expectedDate);
    }

    @Test
    void shouldExtractDateFromClippingMatter() {
        ClippingRequest clippingRequest = new ClippingRequest();
        clippingRequest.setClassificationType(ClassificationType.HEARING);
        clippingRequest.setClippingMatter("Conciliação para a data de 20/07/2020 às 10:00h");
        clippingRequest.setClippingDate(LocalDate.of(2020, 7, 21));

        HearingAppointment h = new HearingAppointment(clippingRequest);
        LocalDate actualDate = h.getClassifiedDate();
        assertThat(actualDate).isEqualTo(LocalDate.of(2020, 7, 20));
    }

    @Test
    void shouldUseDate3BusinessDaysFromClippingDate() {
        ClippingRequest clippingRequest = new ClippingRequest();
        clippingRequest.setClassificationType(ClassificationType.HEARING);
        clippingRequest.setClippingMatter("Conciliação");
        clippingRequest.setClippingDate(LocalDate.of(2020, 7, 21));

        HearingAppointment h = new HearingAppointment(clippingRequest);
        LocalDate actualDate = h.getClassifiedDate();
        assertThat(actualDate).isEqualTo(LocalDate.of(2020, 7, 24));
    }

}