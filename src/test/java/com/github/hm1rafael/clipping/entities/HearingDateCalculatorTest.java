package com.github.hm1rafael.clipping.entities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

class HearingDateCalculatorTest {

    @ParameterizedTest
    @MethodSource("provideDates")
    void datesShouldBeRolledToThreeDaysInTheFuture(LocalDate inputDate, LocalDate expectedDate) {
        LocalDate actualDate = HearingDateCalculator.calculateNextBusinessDate(inputDate);
        Assertions.assertThat(actualDate)
                .isEqualTo(expectedDate);
    }

    private static Stream<Arguments> provideDates() {
        return Stream.of(
                Arguments.of(LocalDate.of(2020, 7, 6), LocalDate.of(2020, 7, 9)),
                Arguments.of(LocalDate.of(2020, 7, 7), LocalDate.of(2020, 7, 10)),
                Arguments.of(LocalDate.of(2020, 7, 8), LocalDate.of(2020, 7, 13)),
                Arguments.of(LocalDate.of(2020, 7, 9), LocalDate.of(2020, 7, 13)),
                Arguments.of(LocalDate.of(2020, 7, 10), LocalDate.of(2020, 7, 13))
        );
    }

}