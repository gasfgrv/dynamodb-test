package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.github.gasfgrv.dynamodbtest.mocks.MusicMock;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.amazonaws.services.dynamodbv2.model.KeyType.HASH;
import static com.amazonaws.services.dynamodbv2.model.KeyType.RANGE;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DynamoDBMusicRepositoryIntegrationTest {

    @Autowired
    private DynamoDBMusicRepository musicRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName
            .parse("localstack/localstack:latest");

    @Container
    private static final LocalStackContainer CONTAINER = new LocalStackContainer(DOCKER_IMAGE_NAME)
            .withServices(DYNAMODB);

    @DynamicPropertySource
    private static void awsProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.serviceEndpoint", () -> CONTAINER.getEndpointOverride(DYNAMODB));
        registry.add("aws.signingRegion", CONTAINER::getRegion);
        registry.add("aws.accessKey", CONTAINER::getAccessKey);
        registry.add("aws.secretKey", CONTAINER::getSecretKey);
    }

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
        createTable();

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
    }

    private void createTable() {
        var attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(
                new AttributeDefinition()
                        .withAttributeName("SongTitle")
                        .withAttributeType(S)
        );
        attributeDefinitions.add(
                new AttributeDefinition()
                        .withAttributeName("Artist")
                        .withAttributeType(S)
        );

        var keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(
                new KeySchemaElement()
                        .withAttributeName("SongTitle")
                        .withKeyType(HASH));
        keySchema.add(
                new KeySchemaElement()
                        .withAttributeName("Artist")
                        .withKeyType(RANGE));

        var tableName = "tb_music";
        var provisionedThroughput = new ProvisionedThroughput(1L, 1L);

        amazonDynamoDB
                .createTable(attributeDefinitions, tableName, keySchema, provisionedThroughput);
    }
}
