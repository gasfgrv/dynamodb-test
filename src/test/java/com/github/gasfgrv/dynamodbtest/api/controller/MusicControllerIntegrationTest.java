package com.github.gasfgrv.dynamodbtest.api.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import io.restassured.RestAssured;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.createTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.deleteTable;
import static com.github.gasfgrv.dynamodbtest.utils.DynamoDBUtils.insertInTable;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.LOCATION;

class MusicControllerIntegrationTest extends GenericIntegrationTestConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        RestAssured.basePath = "/music";
        createTable(amazonDynamoDB);
    }

    @AfterEach
    void tearDown() {
        deleteTable(amazonDynamoDB);
    }

    @Test
    void testSaveMusic() {
        var music = MusicMock.getMusic();

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(music)

                // when
                .when()
                .log()
                .everything()
                .post()

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(201)
                .header(LOCATION, containsString(music.getSongTitle().replaceFirst("\\s", "%20")))
                .header(LOCATION, containsString(music.getArtist()))
                .body("song_title", equalTo(music.getSongTitle()))
                .body("artist", equalTo(music.getArtist()))
                .body("written_by", not(equalTo(music.getWrittenBy())))
                .body("produced_by", not(equalTo(music.getProducedBy())))
                .body("album", equalTo(music.getAlbum()))
                .body("released_in", equalTo(music.getReleasedIn()));
    }

    @Test
    void testSaveSongIfUnknownArtist() {
        var music = MusicMock.getMusic();
        music.setArtist(null);

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(music)

                // when
                .when()
                .log()
                .everything()
                .post()

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(201)
                .header(LOCATION, containsString(music.getSongTitle().replaceFirst("\\s", "%20")))
                .header(LOCATION, containsString("Unknown Artist".replaceFirst("\\s", "%20")))
                .body("song_title", equalTo(music.getSongTitle()))
                .body("artist", equalTo("Unknown Artist"))
                .body("written_by", not(equalTo(music.getWrittenBy())))
                .body("produced_by", not(equalTo(music.getProducedBy())))
                .body("album", equalTo(music.getAlbum()))
                .body("released_in", equalTo(music.getReleasedIn()));
    }

    @Test
    void testSaveSongIfUnknownAlbum() {
        var music = MusicMock.getMusic();
        music.setAlbum(null);

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(music)

                // when
                .when()
                .log()
                .everything()
                .post()

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(201)
                .header(LOCATION, containsString(music.getSongTitle().replaceFirst("\\s", "%20")))
                .header(LOCATION, containsString(music.getArtist()))
                .body("song_title", equalTo(music.getSongTitle()))
                .body("artist", equalTo(music.getArtist()))
                .body("written_by", not(equalTo(music.getWrittenBy())))
                .body("produced_by", not(equalTo(music.getProducedBy())))
                .body("album", equalTo("Unknown Album"))
                .body("released_in", equalTo(music.getReleasedIn()));
    }

    @Test
    void testSaveASongIfUnknownReleaseYear() {
        var music = MusicMock.getMusic();
        music.setReleasedIn(null);

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(music)

                // when
                .when()
                .log()
                .everything()
                .post()

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(201)
                .header(LOCATION, containsString(music.getSongTitle().replaceFirst("\\s", "%20")))
                .header(LOCATION, containsString(music.getArtist()))
                .body("song_title", equalTo(music.getSongTitle()))
                .body("artist", equalTo(music.getArtist()))
                .body("written_by", not(equalTo(music.getWrittenBy())))
                .body("produced_by", not(equalTo(music.getProducedBy())))
                .body("album", equalTo(music.getAlbum()))
                .body("released_in", equalTo(0));
    }

    @Test
    void testFindOneSong() {
        var music = MusicMock.getMusic();
        insertInTable(amazonDynamoDB, music);

        var queryParams = new HashMap<String, String>();
        queryParams.put("song_title", music.getSongTitle());
        queryParams.put("artist", music.getArtist());

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/find")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(200)
                .body("song_title", equalTo(music.getSongTitle()))
                .body("artist", equalTo(music.getArtist()))
                .body("written_by", equalTo(music.getWrittenBy()))
                .body("produced_by", equalTo(music.getProducedBy()))
                .body("album", equalTo(music.getAlbum()))
                .body("released_in", equalTo(music.getReleasedIn()));
    }

    @Test
    void testFindOneSongThrowingMusicNotFoundException() {
        var music = MusicMock.getMusic();

        var queryParams = new HashMap<String, String>();
        queryParams.put("song_title", music.getSongTitle());
        queryParams.put("artist", music.getArtist());

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/find")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(404);
    }

    @Test
    void testFilterMusicWithArtist() {
        var musics = MusicMock.getMusics();
        musics.forEach(music -> insertInTable(amazonDynamoDB, music));

        var queryParams = new HashMap<String, String>();
        queryParams.put("song_title", musics.get(0).getSongTitle());
        queryParams.put("artist", musics.get(0).getArtist());

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/filter")
                .then()

                // then
                .assertThat()
                .log()
                .everything()
                .statusCode(200)
                .body("size()", is(1))
                .body("song_title[0]", equalTo(musics.get(0).getSongTitle()))
                .body("artist[0]", equalTo(musics.get(0).getArtist()))
                .body("album[0]", equalTo(musics.get(0).getAlbum()))
                .body("released_in[0]", equalTo(musics.get(0).getReleasedIn()));
    }

    @Test
    void testFilterMusicWithoutArtist() {
        var musics = MusicMock.getMusics();
        musics.forEach(music -> insertInTable(amazonDynamoDB, music));

        var queryParams = new HashMap<String, String>();
        queryParams.put("song_title", musics.get(0).getSongTitle());

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/filter")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(200)
                .body("size()", is(2))
                .body("song_title[0]", equalTo(musics.get(0).getSongTitle()))
                .body("artist[0]", equalTo(musics.get(0).getArtist()))
                .body("album[0]", equalTo(musics.get(0).getAlbum()))
                .body("released_in[0]", equalTo(musics.get(0).getReleasedIn()))
                .body("song_title[1]", equalTo(musics.get(1).getSongTitle()))
                .body("artist[1]", equalTo(musics.get(1).getArtist()))
                .body("album[1]", equalTo(musics.get(1).getAlbum()))
                .body("released_in[1]", equalTo(musics.get(1).getReleasedIn()));
    }

    @Test
    void testFilterMusicsEmpty() {
        var music = MusicMock.getMusic();

        var queryParams = new HashMap<String, String>();
        queryParams.put("song_title", music.getSongTitle());
        queryParams.put("artist", music.getArtist());

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/filter")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testFindMusicBy() {
        var musics = MusicMock.getMusics();
        musics.forEach(music -> insertInTable(amazonDynamoDB, music));

        var queryParams = new HashMap<String, String>();
        queryParams.put("written_by", musics.get(0).getWrittenBy().get(0));

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/findBy")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void testFindMusicByBadRequest() {
        var musics = MusicMock.getMusics();
        musics.forEach(music -> insertInTable(amazonDynamoDB, music));

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)

                // when
                .when()
                .log()
                .everything()
                .get("/findBy")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(400);
    }

    @Test
    void testFindMusicByEmpty() {
        var musics = MusicMock.getMusics();

        var queryParams = new HashMap<String, String>();
        queryParams.put("written_by", musics.get(0).getWrittenBy().get(0));

        // given
        RestAssured
                .given()
                .log()
                .everything()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)

                // when
                .when()
                .log()
                .everything()
                .get("/findBy")

                // then
                .then()
                .log()
                .everything()
                .assertThat()
                .statusCode(200)
                .body("size()", is(0));
    }

}
