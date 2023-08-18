package com.github.gasfgrv.dynamodbtest.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import java.util.ArrayList;
import lombok.experimental.UtilityClass;

import static com.amazonaws.services.dynamodbv2.model.KeyType.HASH;
import static com.amazonaws.services.dynamodbv2.model.KeyType.RANGE;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;

@UtilityClass
public class DynamoDBUtils {

    public void createTable(AmazonDynamoDB amazonDynamoDB) {
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

    public void deleteTable(AmazonDynamoDB amazonDynamoDB) {
        var tableName = "tb_music";
        amazonDynamoDB.deleteTable(tableName);
    }

}
