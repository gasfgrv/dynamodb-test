package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.createTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.deleteTable;
import static org.assertj.core.api.Assertions.assertThat;

class DynamoDBMusicRepositoryIntegrationTest extends GenericIntegrationTestConfiguration {

    @Autowired
    private DynamoDBMusicRepository musicRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @BeforeEach
    void setUp() {
        createTable(amazonDynamoDB);
    }

    @AfterEach
    void tearDown() {
        deleteTable(amazonDynamoDB);
    }

    @Test
    void testInsertMusic() {
        var music = MusicMock.getMusic();
        musicRepository.insertMusic(music);

        var loadMusic = musicRepository
                .loadMusic(music.getSongTitle(), music.getArtist());

        assertThat(loadMusic)
                .usingRecursiveComparison()
                .isEqualTo(music);
    }

    @Test
    void testLoadMusic() {
        var expected = MusicMock.getMusic();
        musicRepository.insertMusic(expected);

        var actual = musicRepository
                .loadMusic("Falso Realismo", "Jambu");

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void testQueryMusicsWithAndWithoutArtist() {
        var musics = MusicMock.getMusics();
        musics.forEach(musicRepository::insertMusic);

        var queryMusicsWithArtist = musicRepository
                .queryMusics("Ain't No Sunshine", "Bill Withers")
                .stream()
                .toList();

        assertThat(queryMusicsWithArtist)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(musics.get(0));

        var queryMusicsWithoutArtist = musicRepository
                .queryMusics("Ain't No Sunshine", null)
                .stream()
                .toList();

        assertThat(queryMusicsWithoutArtist)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(musics);
    }

}
