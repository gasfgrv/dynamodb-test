package com.github.gasfgrv.dynamodbtest.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@Testcontainers
public class GenericIntegrationTestConfiguration {

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName
            .parse("localstack/localstack:latest");

    @Container
    protected static final LocalStackContainer CONTAINER = new LocalStackContainer(DOCKER_IMAGE_NAME)
            .withServices(DYNAMODB);

    @DynamicPropertySource
    private static void awsProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.serviceEndpoint", () -> CONTAINER.getEndpointOverride(DYNAMODB));
        registry.add("aws.signingRegion", CONTAINER::getRegion);
        registry.add("aws.accessKey", CONTAINER::getAccessKey);
        registry.add("aws.secretKey", CONTAINER::getSecretKey);
    }

}
