package com.github.gasfgrv.dynamodbtest.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GenericIntegrationTestConfiguration {

    private static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName
            .parse("localstack/localstack:latest");

    protected static LocalStackContainer localStackContainer;

    static {
        localStackContainer = new LocalStackContainer(DOCKER_IMAGE_NAME)
                .withServices(DYNAMODB)
                .withReuse(true);

        localStackContainer.start();
    }


    @DynamicPropertySource
    private static void awsProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.serviceEndpoint", () -> localStackContainer.getEndpointOverride(DYNAMODB));
        registry.add("aws.signingRegion", localStackContainer::getRegion);
        registry.add("aws.accessKey", localStackContainer::getAccessKey);
        registry.add("aws.secretKey", localStackContainer::getSecretKey);
    }

}
