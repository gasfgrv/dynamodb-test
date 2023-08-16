package com.github.gasfgrv.dynamodbtest.domain.service;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
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
        if (music.getArtist() == null) {
            music.unknownArtist();
        }

        if (music.getAlbum() == null) {
            music.unknownAlbum();
        }

        if (music.getReleasedIn() == null) {
            music.unknownYear();
        }

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

}
