package com.github.gasfgrv.dynamodbtest.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public DynamoDBMapper dynamoDBMapper() {
        var credentials = new BasicAWSCredentials(accessKey, secretKey);
        var credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        var endpointConfiguration = new EndpointConfiguration(serviceEndpoint, signingRegion);
        var dynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .withCredentials(credentialsProvider)
                .build();
        return new DynamoDBMapper(dynamoDB);
    }

}
