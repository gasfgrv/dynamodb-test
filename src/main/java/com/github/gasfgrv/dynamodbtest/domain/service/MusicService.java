package com.github.gasfgrv.dynamodbtest.domain.service;

import com.github.gasfgrv.dynamodbtest.commons.utils.MusicUtils;
import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.domain.exception.NullFieldsException;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.repository.MusicRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public MusicEntity addASong(MusicEntity music) {
        log.info("Checking if artist is informed, otherwise it will be saved as 'Unknown Artist'");
        MusicUtils.checkIfUnknownArtist(music);

        log.info("Checking if album is informed, otherwise it will be saved as 'Unknown Album'");
        MusicUtils.checkIfUnknownAlbum(music);

        log.info("Checking if release year is informed, otherwise it will be saved as 0 (Unknown Year)");
        MusicUtils.checkIfUnknownReleaseYear(music);

        log.info("Getting the first and last names of who wrote this song");
        var writtenBy = MusicUtils.formatNames(music.getWrittenBy());
        music.setWrittenBy(writtenBy);

        log.info("Getting the first and last names of who produced this song");
        var producedBy = MusicUtils.formatNames(music.getProducedBy());
        music.setProducedBy(producedBy);

        musicRepository.insertMusic(music);

        return music;
    }

    public MusicEntity findOneSong(String songName, String artist) {
        return Optional
                .ofNullable(musicRepository.loadMusic(songName, artist))
                .orElseThrow(() -> new MusicNotFoundException("Sorry, but I can't find this music"));
    }

    public List<MusicEntity> filterMusics(String songName, String artist) {
        var musics = musicRepository.queryMusics(songName, artist);

        if (musics.isEmpty()) {
            log.warn("Filtering could not find any songs, returning an empty list");
            return Collections.emptyList();
        }

        log.info("Filter found {} songs", musics.size());
        return musics;
    }

    public List<MusicEntity> findBy(Map<String, String> fields) {
        log.info("Checking if all search fields are null");
        var allFieldsAreNull = MusicUtils.checkIfAllFieldsAreNull(fields);

        if (allFieldsAreNull) {
            log.error("All parameters are null, at least one of them need to be filled");
            throw new NullFieldsException(
                    "Please enter at least one of the following parameters: 'album', 'produced_by', 'released_in', 'written_by'"
            );
        }

        var fieldsToSearch = MusicUtils.filterNonNullFiels(fields);
        return musicRepository.scanMusics(fieldsToSearch);
    }


}
