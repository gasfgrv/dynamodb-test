package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.createTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.deleteTable;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DynamoDBMusicRepositoryIntegrationTest extends GenericIntegrationTestConfiguration {

    @Autowired
    private DynamoDBMusicRepository musicRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @BeforeAll
    static void beforeAll() {
        CONTAINER.start();
    }

    @AfterAll
    static void afterAll() {
        CONTAINER.stop();
    }

    @Test
    @Order(1)
    void testInsertMusic() throws InterruptedException {
        createTable(amazonDynamoDB);

        var music = MusicMock.getMusic();
        musicRepository.insertMusic(music);

        var loadMusic = musicRepository
                .loadMusic(music.getSongTitle(), music.getArtist());

        assertThat(loadMusic)
                .usingRecursiveComparison()
                .isEqualTo(music);
    }

    @Test
    @Order(2)
    void testLoadMusic() {
        var expected = MusicMock.getMusic();

        var actual = musicRepository
                .loadMusic("Falso Realismo", "Jambu");

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    @Order(3)
    void testQueryMusicsWithAndWithoutArtist() {
        var musics = MusicMock.getMusics();

        musics.forEach(musicRepository::insertMusic);

        var queryMusicsWithArtist = musicRepository
                .queryMusics("Ain't No Sunshine", "Bill Withers")
                .stream()
                .toList();

        var queryMusicsWithoutArtist = musicRepository
                .queryMusics("Ain't No Sunshine", null)
                .stream()
                .toList();

        assertThat(queryMusicsWithArtist)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(musics.get(0));

        assertThat(queryMusicsWithoutArtist)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .hasSameElementsAs(musics);

        deleteTable(amazonDynamoDB);
    }

}
