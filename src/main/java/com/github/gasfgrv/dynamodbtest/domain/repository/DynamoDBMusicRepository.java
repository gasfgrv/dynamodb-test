package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.gasfgrv.dynamodbtest.commons.utils.DynamoDBUtils;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DynamoDBMusicRepository implements MusicRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Override
    public void insertMusic(MusicEntity music) {
        log.info("Saving music in DynamoDB: [songTitle={}, artist={}]", music.getSongTitle(), music.getArtist());
        dynamoDBMapper.save(music);
    }

    @Override
    public MusicEntity loadMusic(String songTitle, String artist) {
        log.info("Loading music in DynamoDB: [songTitle={}, artist={}]", songTitle, artist);
        return dynamoDBMapper.load(MusicEntity.class, songTitle, artist);
    }

    @Override
    public List<MusicEntity> queryMusics(String songTitle, String artist) {
        log.info("Setting key condition from song title: [SongTitle = {}]", songTitle);
        var keyConditions = "SongTitle = :song_title";
        var attributes = new HashMap<String, AttributeValue>();
        attributes.put(":song_title", new AttributeValue().withS(songTitle));

        if (artist != null) {
            log.info("Setting key condition from artist: [Artist = {}]", artist);
            keyConditions += " and Artist = :artist";
            attributes.put(":artist", new AttributeValue().withS(artist));
        }

        log.info("Mounting of query expression: [{}]", keyConditions);
        var queryExpression = new DynamoDBQueryExpression<MusicEntity>()
                .withKeyConditionExpression(keyConditions)
                .withExpressionAttributeValues(attributes);

        log.info("Querying music in DynamoDB");
        return dynamoDBMapper.query(MusicEntity.class, queryExpression);
    }

    @Override
    public List<MusicEntity> scanMusics(Map<String, String> fieldsToSearch) {
        var filterConditions = new ArrayList<String>();
        var attributes = new HashMap<String, AttributeValue>();

        fieldsToSearch
                .forEach((key, value) -> {
                    log.info("Setting filter condition from song title: [{} = {}]", key, value);
                    DynamoDBUtils.setConditions(key, filterConditions);
                    DynamoDBUtils.setAttributes(key, value, attributes);
                });

        log.info("Gathering filter conditions");
        var joinedConditions = String.join(" and ", filterConditions);

        log.info("Mounting of filter expression: [{}]", joinedConditions);
        var scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(joinedConditions)
                .withExpressionAttributeValues(attributes);

        log.info("Scanning table in DynamoDB for search music");
        return dynamoDBMapper.scan(MusicEntity.class, scanExpression);
    }

}
