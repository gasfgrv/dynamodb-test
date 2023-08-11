package com.github.gasfgrv.dynamodbtest.domain.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.HashMap;
import java.util.List;
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
        var atrributes = new HashMap<String, AttributeValue>();
        atrributes.put(":v1", new AttributeValue().withS(songTitle));
        atrributes.put(":v2", new AttributeValue().withS(artist));

        var queryExpression = new DynamoDBQueryExpression<MusicEntity>()
                .withKeyConditionExpression("SongTitle = :v1 and Artist = :v2")
                .withExpressionAttributeValues(atrributes);

        return dynamoDBMapper.query(MusicEntity.class, queryExpression);
    }

}
