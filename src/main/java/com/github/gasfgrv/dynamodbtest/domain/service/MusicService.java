package com.github.gasfgrv.dynamodbtest.domain.service;

import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.repository.MusicRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public MusicEntity addASong(MusicEntity music) {
        checkIfUnknownArtist(music);
        checkIfUnknownAlbum(music);
        checkIfUnknownReleaseYear(music);

        music.setWrittenBy(formatNames(music.getWrittenBy()));
        music.setProducedBy(formatNames(music.getProducedBy()));

        musicRepository.insertMusic(music);

        return music;
    }

    public MusicEntity findOneSong(String songName, String artist) {
        return Optional
                .ofNullable(musicRepository.loadMusic(songName, artist))
                .orElseThrow(() -> new MusicNotFoundException("Sorry but I can't find this music"));
    }

    public List<MusicEntity> filterMusics(String songName, String artist) {
        var musics = musicRepository.queryMusics(songName, artist);

        if (musics.isEmpty()) {
            return Collections.emptyList();
        }

        return musics;
    }

    private List<String> formatNames(List<String> names) {
        return names
                .stream()
                .map(this::getFirstAndLastName)
                .toList();
    }

    private String getFirstAndLastName(String name) {
        if (!name.matches(".*\\s.*")) {
            return name;
        }

        var fullName = name.split("\\s");
        var firstName = fullName[0];
        var lastName = fullName[fullName.length - 1];
        return "%s %s".formatted(firstName, lastName);
    }

    private static void checkIfUnknownReleaseYear(MusicEntity music) {
        Optional
                .ofNullable(music.getReleasedIn())
                .ifPresentOrElse(integer -> {}, music::unknownYear);
    }

    private static void checkIfUnknownAlbum(MusicEntity music) {
        Optional
                .ofNullable(music.getAlbum())
                .ifPresentOrElse(integer -> {}, music::unknownAlbum);
    }

    private static void checkIfUnknownArtist(MusicEntity music) {
        Optional
                .ofNullable(music.getArtist())
                .ifPresentOrElse(integer -> {}, music::unknownArtist);
    }

}
