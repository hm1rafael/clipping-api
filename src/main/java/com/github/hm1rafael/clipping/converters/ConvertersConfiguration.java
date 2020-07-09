package com.github.hm1rafael.clipping.converters;

import com.google.cloud.Timestamp;
import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreCustomConversions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
public class ConvertersConfiguration {

    @Bean
    public DatastoreCustomConversions datastoreCustomConversions(List<Converter> customConverters) {
        return new DatastoreCustomConversions(customConverters);
    }

    @Component
    private static class LocalDateTimeStampConverter implements Converter<Timestamp, LocalDate> {
        private final Clock clock;

        public LocalDateTimeStampConverter(Clock clock) {
            this.clock = clock;
        }

        @Override
        public LocalDate convert(Timestamp timestamp) {
            return Optional.ofNullable(timestamp)
                    .map(Timestamp::toDate)
                    .map(Date::toInstant)
                    .map(i -> i.atZone(ZoneId.systemDefault()))
                    .map(ZonedDateTime::toLocalDate)
                    .orElse(LocalDate.now(clock));
        }
    }

    @Component
    private static class TimeStampLocalDateConverter implements Converter<LocalDate, Timestamp> {
        @Override
        public Timestamp convert(LocalDate localDate) {
            return Optional.ofNullable(localDate)
                    .map(LocalDate::atStartOfDay)
                    .map(d -> d.atZone(ZoneId.systemDefault()))
                    .map(ChronoZonedDateTime::toInstant)
                    .map(Date::from)
                    .map(Timestamp::of)
                    .orElse(null);
        }
    }

}
