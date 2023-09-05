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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicService {

    private final MusicRepository musicRepository;

    public MusicEntity addASong(MusicEntity music) {
        MusicUtils.checkIfUnknownArtist(music);
        MusicUtils.checkIfUnknownAlbum(music);
        MusicUtils.checkIfUnknownReleaseYear(music);

        var writtenBy = MusicUtils.formatNames(music.getWrittenBy());
        music.setWrittenBy(writtenBy);

        var producedBy = MusicUtils.formatNames(music.getProducedBy());
        music.setProducedBy(producedBy);

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

    public List<MusicEntity> findBy(Map<String, String> fields) {
        var allFieldsAreNull = MusicUtils.checkIfAllFieldsAreNull(fields);

        if (allFieldsAreNull) {
            throw new NullFieldsException(
                    "Please enter at least one of the following parameters: 'album', 'produced_by', 'released_in', 'written_by'"
            );
        }

        var fieldsToSearch = MusicUtils.filterNonNullFiels(fields);
        return musicRepository.scanMusics(fieldsToSearch);
    }


}
