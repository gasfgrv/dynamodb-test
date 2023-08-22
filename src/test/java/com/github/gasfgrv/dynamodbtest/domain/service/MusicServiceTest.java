package com.github.gasfgrv.dynamodbtest.domain.service;

import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.repository.MusicRepository;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MusicServiceTest {

    @InjectMocks
    private MusicService musicService;

    @Mock
    private MusicRepository musicRepository;


    @Test
    void testAddASong() {
        var music = MusicMock.getMusic();

        willDoNothing()
                .given(musicRepository)
                .insertMusic(any(MusicEntity.class));

        var savedMusic = musicService.addASong(music);

        assertThat(savedMusic)
                .isNotNull()
                .isInstanceOf(MusicEntity.class)
                .usingRecursiveComparison()
                .isEqualTo(music);

        verify(musicRepository, times(1))
                .insertMusic(music);
    }

    @Test
    void testAddASongIfUnknownArtist() {
        var music = MusicMock.getMusic();
        music.setArtist(null);

        willDoNothing()
                .given(musicRepository)
                .insertMusic(any(MusicEntity.class));

        var savedMusic = musicService.addASong(music);

        assertThat(savedMusic)
                .isNotNull()
                .isInstanceOf(MusicEntity.class)
                .hasFieldOrPropertyWithValue("artist", "Unknown Artist");

        verify(musicRepository, times(1))
                .insertMusic(music);
    }

    @Test
    void testAddASongIfUnknownAlbum() {
        var music = MusicMock.getMusic();
        music.setAlbum(null);

        willDoNothing()
                .given(musicRepository)
                .insertMusic(any(MusicEntity.class));

        var savedMusic = musicService.addASong(music);

        assertThat(savedMusic)
                .isNotNull()
                .isInstanceOf(MusicEntity.class)
                .hasFieldOrPropertyWithValue("album", "Unknown Album");

        verify(musicRepository, times(1))
                .insertMusic(music);
    }

    @Test
    void testAddASongIfUnknownReleaseYear() {
        var music = MusicMock.getMusic();
        music.setReleasedIn(null);

        willDoNothing()
                .given(musicRepository)
                .insertMusic(any(MusicEntity.class));

        var savedMusic = musicService.addASong(music);

        assertThat(savedMusic)
                .isNotNull()
                .isInstanceOf(MusicEntity.class)
                .hasFieldOrPropertyWithValue("releasedIn", 0);

        verify(musicRepository, times(1))
                .insertMusic(music);
    }

    @Test
    void testFindOneSong() {
        var music = MusicMock.getMusic();

        willReturn(music)
                .given(musicRepository)
                .loadMusic(anyString(), anyString());

        var findMusic = musicService
                .findOneSong(music.getSongTitle(), music.getArtist());

        assertThat(findMusic)
                .isInstanceOf(MusicEntity.class)
                .hasFieldOrPropertyWithValue("songTitle", "Falso Realismo")
                .hasFieldOrPropertyWithValue("artist", "Jambu");

        verify(musicRepository, times(1))
                .loadMusic(music.getSongTitle(), music.getArtist());
    }

    @Test
    void testFindOneSongThrowingMusicNotFoundException() {
        willReturn(null)
                .given(musicRepository)
                .loadMusic(anyString(), anyString());

        assertThatExceptionOfType(MusicNotFoundException.class)
                .isThrownBy(() ->
                        musicService.findOneSong("Evidências", "Chitãozinho & Xororó"))
                .withMessage("Sorry but I can't find this music");
    }

    @Test
    void testFilterMusics() {
        var musics = MusicMock.getMusics();

        willReturn(musics)
                .given(musicRepository)
                .queryMusics(anyString(), any());

        var findMusic = musicService
                .filterMusics("Ain't No Sunshine", null);

        assertThat(findMusic)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(musics);

        verify(musicRepository, times(1))
                .queryMusics("Ain't No Sunshine", null);
    }

    @Test
    void testFilterMusicsEmpty() {
        willReturn(Collections.emptyList())
                .given(musicRepository)
                .queryMusics(anyString(), anyString());

        var findMusic = musicService
                .filterMusics("Evidências", "Chitãozinho & Xororó");

        assertThat(findMusic)
                .isEmpty();

        verify(musicRepository, times(1))
                .queryMusics("Evidências", "Chitãozinho & Xororó");
    }


}