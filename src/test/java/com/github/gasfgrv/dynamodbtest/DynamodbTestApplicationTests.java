package com.github.gasfgrv.dynamodbtest;

import com.github.gasfgrv.dynamodbtest.config.GenericIntegrationTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class DynamodbTestApplicationTests extends GenericIntegrationTestConfiguration {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context.containsBean("openAPI")).isTrue();
        assertThat(context.containsBean("modelMapper")).isTrue();
        assertThat(context.containsBean("awsCredentials")).isTrue();
        assertThat(context.containsBean("awsCredentialsProvider")).isTrue();
        assertThat(context.containsBean("amazonDynamoDBClient")).isTrue();
        assertThat(context.containsBean("dynamoDBMapper")).isTrue();
    }

}
