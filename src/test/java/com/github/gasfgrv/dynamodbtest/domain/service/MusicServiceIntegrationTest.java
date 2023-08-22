package com.github.gasfgrv.dynamodbtest.domain.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.createTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.deleteTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MusicServiceIntegrationTest extends GenericIntegrationTestConfiguration {

    @Autowired
    private MusicService musicService;

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

    @BeforeEach
    void setUp() {
        createTable(amazonDynamoDB);
    }

    @AfterEach
    void tearDown() {
        deleteTable(amazonDynamoDB);
    }

    @Test
    @Order(1)
    void testAddASong() {
        var music = MusicMock.getMusic();
        var addedSong = musicService.addASong(music);

        assertThat(addedSong)
                .usingRecursiveComparison()
                .ignoringFields("writtenBy", "producedBy")
                .isEqualTo(music);

        assertThat(addedSong.getWrittenBy())
                .hasSize(music.getWrittenBy().size());

        assertThat(addedSong.getProducedBy())
                .hasSize(music.getProducedBy().size());
    }

    @Test
    @Order(2)
    void testAddASongIfUnknownArtist() {
        var music = MusicMock.getMusic();
        music.setArtist(null);

        var addedSong = musicService.addASong(music);

        assertThat(addedSong)
                .usingRecursiveComparison()
                .ignoringFields("writtenBy", "producedBy", "artist")
                .isEqualTo(music);

        assertThat(addedSong.getWrittenBy())
                .hasSize(music.getWrittenBy().size());

        assertThat(addedSong.getProducedBy())
                .hasSize(music.getProducedBy().size());

        assertThat(addedSong.getArtist())
                .isEqualTo("Unknown Artist");
    }

    @Test
    @Order(3)
    void testAddASongIfUnknownAlbum() {
        var music = MusicMock.getMusic();
        music.setAlbum(null);

        var addedSong = musicService.addASong(music);

        assertThat(addedSong)
                .usingRecursiveComparison()
                .ignoringFields("writtenBy", "producedBy", "album")
                .isEqualTo(music);

        assertThat(addedSong.getWrittenBy())
                .hasSize(music.getWrittenBy().size());

        assertThat(addedSong.getProducedBy())
                .hasSize(music.getProducedBy().size());

        assertThat(addedSong.getAlbum())
                .isEqualTo("Unknown Album");
    }

    @Test
    @Order(4)
    void testAddASongIfUnknownReleaseYear() {
        var music = MusicMock.getMusic();
        music.setReleasedIn(null);

        var addedSong = musicService.addASong(music);

        assertThat(addedSong)
                .usingRecursiveComparison()
                .ignoringFields("writtenBy", "producedBy", "releasedIn")
                .isEqualTo(music);

        assertThat(addedSong.getWrittenBy())
                .hasSize(music.getWrittenBy().size());

        assertThat(addedSong.getProducedBy())
                .hasSize(music.getProducedBy().size());

        assertThat(addedSong.getReleasedIn())
                .isZero();
    }

    @Test
    @Order(5)
    void testFindOneSong() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        var musicFound = musicService
                .findOneSong("Falso Realismo", "Jambu");

        assertThat(musicFound)
                .usingRecursiveComparison()
                .ignoringFields("writtenBy", "producedBy")
                .isEqualTo(music);
    }

    @Test
    @Order(6)
    void testFindOneSongThrowingMusicNotFoundException() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        assertThatExceptionOfType(MusicNotFoundException.class)
                .isThrownBy(() -> musicService
                        .findOneSong("Vida Vazia", "Codinome Winchester"))
                .withMessage("Sorry but I can't find this music");
    }

    @Test
    @Order(7)
    void testFilterMusics() {
        var musics = MusicMock.getMusics();
        musics.forEach(musicService::addASong);

        var musicsFoundWithArtist = musicService
                .filterMusics("Ain't No Sunshine", "Bill Withers");

        assertThat(musicsFoundWithArtist)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("writtenBy", "producedBy")
                .contains(musics.get(0));

        var musicsFoundWithoutArtist = musicService
                .filterMusics("Ain't No Sunshine", null);

        assertThat(musicsFoundWithoutArtist)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("writtenBy", "producedBy")
                .isEqualTo(musics);
    }

    @Test
    @Order(8)
    void testFilterMusicsEmpty() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        var musicsFound = musicService
                .filterMusics("Cry Thunder", null);

        assertThat(musicsFound)
                .isEmpty();

    }

}