package com.github.gasfgrv.dynamodbtest.commons.config;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsConfig {

    @Value("${aws.serviceEndpoint}")
    private String serviceEndpoint;

    @Value("${aws.signingRegion}")
    private String signingRegion;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Bean
    public AWSCredentials awsCredentials() {
        log.info("Getting AWS account credentials");
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public AWSCredentialsProvider awsCredentialsProvider() {
        log.info("Getting the AWS Account Credential Provider");
        return new AWSStaticCredentialsProvider(awsCredentials());
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDBClient() {
        log.info("Getting the DynamoDB Service Endpoint and Region");
        var endpointConfiguration = new AwsClientBuilder
                .EndpointConfiguration(serviceEndpoint, signingRegion);

        log.info("Getting the DynamoDB Client");
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(awsCredentialsProvider())
                .build();
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        log.info("Getting the Class Mapper for DynamoDB Tables");
        return new DynamoDBMapper(amazonDynamoDBClient());
    }

}
