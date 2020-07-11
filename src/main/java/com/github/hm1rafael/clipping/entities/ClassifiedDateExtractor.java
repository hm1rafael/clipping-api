package com.github.hm1rafael.clipping.entities;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
class ClassifiedDateExtractor {

    private final List<Extractor> EXTRACTORS;

    static {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter extendDateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));

        EXTRACTORS = List.of(
                    Extractor.of("Conciliação para a data de (\\d{1,2}\\/\\d{1,2}\\/\\d{4}) às (\\d{1,2}:\\d{2})h", dateTimeFormatter),
                    Extractor.of("Audiencia para a data de (\\d{1,2}\\/\\d{1,2}\\/\\d{4}) às (\\d{1,2}:\\d{2})h", dateTimeFormatter),
                    Extractor.of("Conciliação para a data de (\\d{1,2} de \\w+ de \\d{4}) às (\\d{1,2}:\\d{2})h", extendDateFormatter),
                    Extractor.of("Audiencia para a data de (\\d{1,2} de \\w+ de \\d{4}) às (\\d{1,2}:\\d{2})h", extendDateFormatter)
        );
    }

    Optional<Pair<LocalDate, String>> extract(String originalText) {
        if (StringUtils.isBlank(originalText)) {
            return Optional.empty();
        }
        String text = Jsoup.parse(originalText).text();
        return EXTRACTORS.stream()
                .map(extractor -> extractor.extract(text))
                .filter(Objects::nonNull)
                .findFirst();
    }

    private static class Extractor {
        private final Pattern pattern;
        private final DateTimeFormatter dateTimeFormatter;

        private Extractor(String pattern, DateTimeFormatter dateTimeFormatter) {
            this.pattern = Pattern.compile(pattern);
            this.dateTimeFormatter = dateTimeFormatter;
        }

        public static Extractor of(String pattern, DateTimeFormatter dateTimeFormatter) {
            return new Extractor(pattern, dateTimeFormatter);
        }

        public Pair<LocalDate, String> extract(String text) {
            Matcher matcher = this.pattern.matcher(text);
            if (matcher.find()) {
                LocalDate parse = LocalDate.parse(matcher.group(1), dateTimeFormatter);
                return Pair.of(parse, matcher.group(2));
            }
            return null;
        }
    }

}
