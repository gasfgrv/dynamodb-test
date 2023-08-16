package com.github.gasfgrv.dynamodbtest.domain.dao;

import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.List;

public interface MusicRepository {

    void insertMusic(MusicEntity music);

    MusicEntity loadMusic(String artist, String songTitle);

    List<MusicEntity> queryMusics(String artist, String songTitle);

}