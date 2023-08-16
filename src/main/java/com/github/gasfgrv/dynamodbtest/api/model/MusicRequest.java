package com.github.gasfgrv.dynamodbtest.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MusicRequest {

    @NotBlank
    private String songTitle;
    private String artist;
    private List<String> writtenBy;
    private List<String> producedBy;
    private String album;
    private Integer releasedIn;

}
