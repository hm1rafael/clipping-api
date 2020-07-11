package com.github.hm1rafael.clipping.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

class ClassifiedDateExtractorTest {

    @ParameterizedTest
    @MethodSource("provideDatesAndTexts")
    void extractDateAndTimeFromText(LocalDate date, String time, String text) {
        ClassifiedDateExtractor.extract(text)
                .ifPresentOrElse(pair -> validateValues(pair, date, time), () -> Assertions.fail("Nothing extracted. Values expected %s and %s", date, time));
    }

    @ParameterizedTest
    @MethodSource("provideTexts")
    void dateNotPresent(String text) {
        ClassifiedDateExtractor.extract(text)
                .ifPresent(pair -> Assertions.fail("should not match. Value found: %s", pair));
    }

    @Test
    void textNull() {
        Optional<Pair<LocalDate, String>> extract = ClassifiedDateExtractor.extract(null);
        Assertions.assertThat(extract.isPresent()).isFalse();
    }

    private void validateValues(Pair<LocalDate, String> actualDateAndTime, LocalDate expectedDate, String expectedTime) {
        Assertions.assertThat(actualDateAndTime.getFirst()).isEqualTo(expectedDate);
        Assertions.assertThat(actualDateAndTime.getSecond()).isEqualTo(expectedTime);
    }

    private static Stream<Arguments> provideDatesAndTexts() {
        return Stream.of(
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Conciliação para a data de 20/07/2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Conciliação <br/> para a data de 20/07/2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Audiencia para a data de 20/07/2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Audiencia <br/> para a data de 20/07/2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Conciliação para a data de 20 de julho de 2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Conciliação para a data de 20 de julho de <br> 2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Audiencia para a data de 20 de julho de 2020 às 10:00h"),
                Arguments.of(LocalDate.of(2020, 7, 20), "10:00", "Audiencia para a data de 20 de                               julho de 2020 às 10:00h")
        );
    }

    private static Stream<Arguments> provideTexts() {
        return Stream.of(
                Arguments.of("para a data de 20/07/2020 às 10:00h"),
                Arguments.of("20/07/2020 às 10:00h"),
                Arguments.of("20 de julho de 2020 às 10:00h"),
                Arguments.arguments("<br>"),
                Arguments.arguments("Conciliação para a data de vinte de julho de <br> 2020 às 10:00h")
        );
    }

}