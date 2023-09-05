package com.github.gasfgrv.dynamodbtest.domain.repository;

import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.List;
import java.util.Map;

public interface MusicRepository {

    void insertMusic(MusicEntity music);

    MusicEntity loadMusic(String artist, String songTitle);

    List<MusicEntity> queryMusics(String artist, String songTitle);

    List<MusicEntity> scanMusics(Map<String, String> fieldsToSearch);
}
