package com.github.gasfgrv.dynamodbtest.domain.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@DynamoDBTable(tableName = "tb_music")
public class MusicEntity {

    @DynamoDBHashKey(attributeName = "SongTitle")
    private String songTitle;
    @DynamoDBRangeKey(attributeName = "Artist")
    private String artist;
    @DynamoDBAttribute(attributeName = "WrittenBy")
    private List<String> writtenBy;
    @DynamoDBAttribute(attributeName = "ProducedBy")
    private List<String> producedBy;
    @DynamoDBAttribute(attributeName = "Album")
    private String album;
    @DynamoDBAttribute(attributeName = "ReleasedIn")
    private Integer releasedIn;

    public void unknownArtist() {
        this.setArtist("Unknown Artist");
    }

    public void unknownAlbum() {
        this.setAlbum("Unknown Album");
    }

    public void unknownYear() {
        this.setReleasedIn(0);
    }

}
