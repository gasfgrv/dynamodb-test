package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class DynamoDBMusicRepositoryTest {

    @InjectMocks
    private DynamoDBMusicRepository musicRepository;

    @Mock
    private DynamoDBMapper dynamoDBMapper;

    @Test
    void testInsertMusic() {
        var music = MusicMock.getMusic();

        willDoNothing()
                .given(dynamoDBMapper)
                .save(eq(music));

        musicRepository.insertMusic(music);

        verify(dynamoDBMapper, times(1))
                .save(music);
    }

    @Test
    void testLoadMusic() {
        willReturn(MusicMock.getMusic())
                .given(dynamoDBMapper)
                .load(eq(MusicEntity.class), anyString(), anyString());

        var music = musicRepository
                .loadMusic("Falso Realismo", "Jambu");

        verify(dynamoDBMapper, times(1))
                .load(MusicEntity.class, "Falso Realismo", "Jambu");

        assertThat(music)
                .isNotNull();
    }

    @Test
    void testQueryMusicsWithArtist() {
        var queryList = mock(PaginatedQueryList.class);

        willReturn(queryList)
                .given(dynamoDBMapper)
                .query(eq(MusicEntity.class), any());

        musicRepository
                .queryMusics("Ain't No Sunshine", "Bill Withers");

        verify(dynamoDBMapper, times(1))
                .query(eq(MusicEntity.class), any());
    }

    @Test
    void testQueryMusicsWithoutArtist() {
        var queryList = mock(PaginatedQueryList.class);

        willReturn(queryList)
                .given(dynamoDBMapper)
                .query(eq(MusicEntity.class), any());

        musicRepository
                .queryMusics("Ain't No Sunshine", null);

        verify(dynamoDBMapper, times(1))
                .query(eq(MusicEntity.class), any());
    }

}
