package com.github.gasfgrv.dynamodbtest.api.controller;

import com.github.gasfgrv.dynamodbtest.api.model.MusicRequest;
import com.github.gasfgrv.dynamodbtest.api.model.MusicResponse;
import com.github.gasfgrv.dynamodbtest.api.model.MusicsResponse;
import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.service.MusicService;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MusicControllerTest {

    @InjectMocks
    private MusicController musicController;

    @Mock
    private MusicService musicService;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        RequestContextHolder
                .setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    void testSaveMusic() {
        var music = MusicMock.getMusic();

        var musicRequest = MusicRequest
                .builder()
                .songTitle(music.getSongTitle())
                .artist(music.getArtist())
                .writtenBy(music.getWrittenBy())
                .producedBy(music.getProducedBy())
                .album(music.getAlbum())
                .releasedIn(music.getReleasedIn())
                .build();

        var responseBody = new MusicResponse();
        responseBody.setSongTitle(music.getSongTitle());
        responseBody.setArtist(music.getArtist());
        responseBody.setWrittenBy(music.getWrittenBy());
        responseBody.setProducedBy(music.getProducedBy());
        responseBody.setAlbum(music.getAlbum());
        responseBody.setReleasedIn(music.getReleasedIn());

        willReturn(music)
                .given(modelMapper)
                .map(eq(musicRequest), eq(MusicEntity.class));

        willReturn(music)
                .given(musicService)
                .addASong(eq(music));

        willReturn(responseBody)
                .given(modelMapper)
                .map(eq(music), eq(MusicResponse.class));

        var response = musicController.saveMusic(musicRequest);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        assertThat(response.getHeaders().get(HttpHeaders.LOCATION))
                .isNotEmpty();

        assertThat(response.getBody())
                .isNotNull()
                .isInstanceOf(MusicResponse.class);

        verify(modelMapper, times(1))
                .map(musicRequest, MusicEntity.class);

        verify(musicService, times(1))
                .addASong(music);

        verify(modelMapper, times(1))
                .map(music, MusicResponse.class);
    }

    @Test
    void testLoadMusic() {
        var music = MusicMock.getMusic();

        var responseBody = new MusicResponse();
        responseBody.setSongTitle(music.getSongTitle());
        responseBody.setArtist(music.getArtist());
        responseBody.setWrittenBy(music.getWrittenBy());
        responseBody.setProducedBy(music.getProducedBy());
        responseBody.setAlbum(music.getAlbum());
        responseBody.setReleasedIn(music.getReleasedIn());

        willReturn(music)
                .given(musicService)
                .findOneSong(eq(music.getSongTitle()), eq(music.getArtist()));

        willReturn(responseBody)
                .given(modelMapper)
                .map(eq(music), eq(MusicResponse.class));

        var response = musicController
                .loadMusic(music.getSongTitle(), music.getArtist());

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotNull()
                .isInstanceOf(MusicResponse.class);


        verify(musicService, times(1))
                .findOneSong(music.getSongTitle(), music.getArtist());

        verify(modelMapper, times(1))
                .map(music, MusicResponse.class);
    }

    @Test
    void testLoadMusicThrowingException() {
        var music = MusicMock.getMusic();

        var exception = new MusicNotFoundException("Sorry but I can't find this music");

        willThrow(exception)
                .given(musicService)
                .findOneSong(anyString(), anyString());

        assertThatExceptionOfType(MusicNotFoundException.class)
                .isThrownBy(() -> musicController
                        .loadMusic("Falso Realismo", "Jambu"))
                .withMessage(exception.getMessage());

        verify(musicService, times(1))
                .findOneSong(music.getSongTitle(), music.getArtist());

        verify(modelMapper, never())
                .map(music, MusicResponse.class);
    }

    @Test
    void testQueryMusics() {
        var musics = MusicMock.getMusics();

        var responseBody = new MusicsResponse();
        responseBody.setSongTitle(musics.get(0).getSongTitle());
        responseBody.setArtist(musics.get(0).getArtist());
        responseBody.setAlbum(musics.get(0).getAlbum());
        responseBody.setReleasedIn(musics.get(0).getReleasedIn());

        willReturn(Collections.singletonList(musics.get(0)))
                .given(musicService)
                .filterMusics(anyString(), anyString());

        willReturn(responseBody)
                .given(modelMapper)
                .map(any(MusicEntity.class), eq(MusicsResponse.class));

        var response = musicController
                .queryMusics("Ain't No Sunshine", "Bill Withers");

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotEmpty();

        verify(musicService, times(1))
                .filterMusics(anyString(), anyString());

        verify(modelMapper, times(1))
                .map(musics.get(0), MusicsResponse.class);
    }

    @Test
    void testQueryMusicsWithoutArtist() {
        var musics = MusicMock.getMusics();

        var responseBody = musics
                .stream()
                .map(music -> {
                    var mappedMusic = new MusicsResponse();
                    mappedMusic.setSongTitle(music.getSongTitle());
                    mappedMusic.setArtist(music.getArtist());
                    mappedMusic.setAlbum(music.getAlbum());
                    mappedMusic.setReleasedIn(music.getReleasedIn());
                    return mappedMusic;
                })
                .toList();

        willReturn(musics)
                .given(musicService)
                .filterMusics(anyString(), isNull());

        willReturn(responseBody.get(0))
                .given(modelMapper)
                .map(musics.get(0), MusicsResponse.class);

        willReturn(responseBody.get(1))
                .given(modelMapper)
                .map(musics.get(1), MusicsResponse.class);

        var response = musicController
                .queryMusics("Ain't No Sunshine", null);

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isNotEmpty()
                .hasSize(2);

        verify(musicService, times(1))
                .filterMusics("Ain't No Sunshine", null);

        verify(modelMapper, times(2))
                .map(any(MusicEntity.class), eq(MusicsResponse.class));
    }

    @Test
    void testQueryMusicsEmpty() {
        var musics = Collections.emptyList();

        willReturn(musics)
                .given(musicService)
                .filterMusics(anyString(), anyString());

        var response = musicController
                .queryMusics("Ain't No Sunshine", "Lighthouse Family");

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isEmpty();

        verify(musicService, times(1))
                .filterMusics(anyString(), anyString());

        verify(modelMapper, never())
                .map(any(MusicEntity.class), eq(MusicsResponse.class));
    }

}