package com.github.gasfgrv.dynamodbtest.commons.utils;

import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MusicUtils {

    private static final Predicate<Map.Entry<String, String>> FILTER_NON_NULL = entry ->
            Objects.nonNull(entry.getValue());
    private static final Predicate<Map.Entry<String, String>> FILTER_NON_EMPTY = entry ->
            !entry.getValue().isEmpty();

    public List<String> formatNames(List<String> names) {
        return names
                .stream()
                .map(MusicUtils::getFirstAndLastNames)
                .toList();
    }

    private static String getFirstAndLastNames(String name) {
        var pattern = Pattern.compile("(^\\b\\w+)|(\\b\\w+$)",
                Pattern.UNICODE_CHARACTER_CLASS);
        var matcher = pattern.matcher(name);

        var firstAndLastNames = matcher
                .results()
                .map(MatchResult::group)
                .toList();

        return String.join(" ", firstAndLastNames);
    }

    public void checkIfUnknownReleaseYear(MusicEntity music) {
        Optional
                .ofNullable(music.getReleasedIn())
                .ifPresentOrElse(music::setReleasedIn, music::unknownYear);
    }

    public void checkIfUnknownAlbum(MusicEntity music) {
        Optional
                .ofNullable(music.getAlbum())
                .ifPresentOrElse(music::setAlbum, music::unknownAlbum);
    }

    public void checkIfUnknownArtist(MusicEntity music) {
        Optional
                .ofNullable(music.getArtist())
                .ifPresentOrElse(music::setArtist, music::unknownArtist);
    }

    public boolean checkIfAllFieldsAreNull(Map<String, String> fields) {
        return fields
                .values()
                .stream()
                .allMatch(Objects::isNull);
    }

    public Map<String, String> filterNonNullFiels(Map<String, String> fields) {
        return fields
                .entrySet()
                .stream()
                .filter(FILTER_NON_NULL)
                .filter(FILTER_NON_EMPTY)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
