package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import java.util.Collections;
import java.util.HashMap;
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
        var iterator = MusicMock.getMusics().iterator();

        willReturn(iterator)
                .given(queryList)
                .iterator();

        willReturn(queryList)
                .given(dynamoDBMapper)
                .query(eq(MusicEntity.class), any());

        var queried = musicRepository
                .queryMusics("Ain't No Sunshine", "Bill Withers");

        assertThat(queried)
                .isNotEmpty();

        verify(dynamoDBMapper, times(1))
                .query(eq(MusicEntity.class), any());
    }

    @Test
    void testQueryMusicsWithoutArtist() {
        var queryList = mock(PaginatedQueryList.class);
        var iterator = MusicMock.getMusics().iterator();

        willReturn(iterator)
                .given(queryList)
                .iterator();

        willReturn(queryList)
                .given(dynamoDBMapper)
                .query(eq(MusicEntity.class), any());

        var queried = musicRepository
                .queryMusics("Ain't No Sunshine", null);

        assertThat(queried)
                .isNotEmpty();

        verify(dynamoDBMapper, times(1))
                .query(eq(MusicEntity.class), any());
    }

    @Test
    void testScanMusic() {
        var music = MusicMock.getMusic();

        var fields = new HashMap<String, String>();
        fields.put("Album", music.getAlbum());
        fields.put("ProducedBy", music.getProducedBy().get(0));
        fields.put("ReleasedIn", String.valueOf(music.getReleasedIn()));
        fields.put("WrittenBy", music.getWrittenBy().get(0));

        var scanList = mock(PaginatedScanList.class);
        var iterator = Collections.singletonList(music).iterator();

        willReturn(iterator)
                .given(scanList)
                .iterator();

        willReturn(scanList)
                .given(dynamoDBMapper)
                .scan(eq(MusicEntity.class), any(DynamoDBScanExpression.class));

        var scan = musicRepository
                .scanMusics(fields);

        assertThat(scan)
                .isNotEmpty();

        verify(dynamoDBMapper, times(1))
                .scan(eq(MusicEntity.class), any(DynamoDBScanExpression.class));
    }

}
