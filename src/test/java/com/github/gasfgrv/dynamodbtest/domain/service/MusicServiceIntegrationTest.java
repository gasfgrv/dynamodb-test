package com.github.gasfgrv.dynamodbtest.domain.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import com.github.gasfgrv.dynamodbtest.domain.exception.MusicNotFoundException;
import com.github.gasfgrv.dynamodbtest.domain.exception.NullFieldsException;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.createTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.deleteTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class MusicServiceIntegrationTest extends GenericIntegrationTestConfiguration {

    @Autowired
    private MusicService musicService;

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
    void testFindOneSongThrowingMusicNotFoundException() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        assertThatExceptionOfType(MusicNotFoundException.class)
                .isThrownBy(() -> musicService
                        .findOneSong("Vida Vazia", "Codinome Winchester"))
                .withMessage("Sorry but I can't find this music");
    }

    @Test
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
    void testFilterMusicsEmpty() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        var musicsFound = musicService
                .filterMusics("Cry Thunder", null);

        assertThat(musicsFound)
                .isEmpty();

    }

    @Test
    void testFindByWithAllFieldsAreNull() {
        var fields = new HashMap<String, String>();
        fields.put("Album", null);
        fields.put("ProducedBy", null);
        fields.put("ReleasedIn", null);
        fields.put("WrittenBy", null);

        assertThatExceptionOfType(NullFieldsException.class)
                .isThrownBy(() -> musicService.findBy(fields))
                .withMessage("Please enter at least one of the following parameters: 'album', 'produced_by', 'released_in', 'written_by'");
    }

    @Test
    void testFindBy() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        var fields = new HashMap<String, String>();
        fields.put("Album", music.getAlbum());
        fields.put("ProducedBy", music.getProducedBy().get(0));
        fields.put("ReleasedIn", String.valueOf(music.getReleasedIn()));
        fields.put("WrittenBy", music.getWrittenBy().get(0));

        var findMusic = musicService
                .findBy(fields);

        assertThat(findMusic)
                .isNotEmpty()
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(music);
    }

    @Test
    void testFindByRetunsEmpty() {
        var music = MusicMock.getMusic();
        musicService.addASong(music);

        var fields = new HashMap<String, String>();
        fields.put("Album", "null");
        fields.put("ProducedBy", "null");
        fields.put("ReleasedIn", "0");
        fields.put("WrittenBy", "null");

        var findMusic = musicService
                .findBy(fields);

        assertThat(findMusic)
                .isEmpty();
    }

}