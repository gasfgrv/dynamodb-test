package com.github.gasfgrv.dynamodbtest.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.amazonaws.services.dynamodbv2.model.KeyType.HASH;
import static com.amazonaws.services.dynamodbv2.model.KeyType.RANGE;
import static com.amazonaws.services.dynamodbv2.model.ScalarAttributeType.S;


public class DynamoDBUtils {

    public static void createTable(AmazonDynamoDB amazonDynamoDB) {
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

    public static void deleteTable(AmazonDynamoDB amazonDynamoDB) {
        var tableName = "tb_music";
        amazonDynamoDB.deleteTable(tableName);
    }

    public static void insertInTable(AmazonDynamoDB amazonDynamoDB, MusicEntity music) {
        var values = new HashMap<String, AttributeValue>();
        values.put("SongTitle", getWithS(music.getSongTitle()));
        values.put("Artist", getWithS(music.getArtist()));
        values.put("WrittenBy", getWithL(music.getWrittenBy()));
        values.put("ProducedBy", getWithL(music.getProducedBy()));
        values.put("Album", getWithS(music.getAlbum()));
        values.put("ReleasedIn", getWithN(music.getReleasedIn()));

        var putItemRequest = new PutItemRequest()
                .withTableName("tb_music")
                .withItem(values);

        amazonDynamoDB.putItem(putItemRequest);
    }

    private static AttributeValue getWithN(Integer integer) {
        return new AttributeValue().withN(integer.toString());
    }

    private static AttributeValue getWithS(String string) {
        return new AttributeValue().withS(string);
    }

    private static AttributeValue getWithL(List<String> list) {
        return new AttributeValue().withL(list.stream().map(s -> new AttributeValue().withS(s)).toList());
    }

}
