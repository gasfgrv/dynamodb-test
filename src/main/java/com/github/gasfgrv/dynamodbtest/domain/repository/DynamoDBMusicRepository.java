package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DynamoDBMusicRepository implements MusicRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Override
    public void insertMusic(MusicEntity music) {
        dynamoDBMapper.save(music);
    }

    @Override
    public MusicEntity loadMusic(String songTitle, String artist) {
        return dynamoDBMapper.load(MusicEntity.class, songTitle, artist);
    }

    @Override
    public List<MusicEntity> queryMusics(String songTitle, String artist) {
        var keyConditionExpression = "SongTitle = :song_title";
        var attributes = new HashMap<String, AttributeValue>();
        attributes.put(":song_title", new AttributeValue().withS(songTitle));

        if (artist != null) {
            keyConditionExpression += " and Artist = :artist";
            attributes.put(":artist", new AttributeValue().withS(artist));
        }

        var queryExpression = new DynamoDBQueryExpression<MusicEntity>()
                .withKeyConditionExpression(keyConditionExpression)
                .withExpressionAttributeValues(attributes);

        return dynamoDBMapper.query(MusicEntity.class, queryExpression);
    }

    @Override
    public List<MusicEntity> scanMusics(Map<String, String> fieldsToSearch) {
        var filterConditions = new ArrayList<String>();
        var attributes = new HashMap<String, AttributeValue>();

        fieldsToSearch
                .forEach((key, value) -> {
                    setConditions(key, filterConditions);
                    setAttributes(key, value, attributes);
                });

        String join = String.join(" and ", filterConditions);

        var scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(join)
                .withExpressionAttributeValues(attributes);
        return dynamoDBMapper.scan(MusicEntity.class, scanExpression);
    }

    private static void setAttributes(String key, String value, HashMap<String, AttributeValue> attributes) {
        switch (key) {
            case "Album", "ProducedBy", "WrittenBy" ->
                    attributes.put(":%s".formatted(key.toLowerCase()), new AttributeValue().withS(value));
            case "ReleasedIn" -> attributes.put(":%s".formatted(key.toLowerCase()), new AttributeValue().withN(value));
            default -> throw new NullPointerException();
        }
    }

    private void setConditions(String key, ArrayList<String> filterConditions) {
        switch (key) {
            case "Album", "ReleasedIn" -> filterConditions.add("%s = :%s".formatted(key, key.toLowerCase()));
            case "ProducedBy", "WrittenBy" ->
                    filterConditions.add("contains(%s, :%s)".formatted(key, key.toLowerCase()));
            default -> throw new NullPointerException();
        }
    }

}
