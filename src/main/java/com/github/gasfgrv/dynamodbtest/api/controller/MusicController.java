package com.github.gasfgrv.dynamodbtest.api.controller;

import com.github.gasfgrv.dynamodbtest.api.model.MusicRequest;
import com.github.gasfgrv.dynamodbtest.api.model.MusicResponse;
import com.github.gasfgrv.dynamodbtest.api.model.MusicsResponse;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.service.MusicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Tag(name = "Music")
public class MusicController {

    private final MusicService musicService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<MusicResponse> saveMusic(@RequestBody @Valid MusicRequest request) {
        var entity = modelMapper.map(request, MusicEntity.class);
        var savedMusic = musicService.addASong(entity);

        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/find")
                .queryParam("song_title", savedMusic.getSongTitle())
                .queryParam("artist", savedMusic.getArtist())
                .build()
                .toUri();

        var musicResponse = modelMapper.map(savedMusic, MusicResponse.class);

        return ResponseEntity.created(location).body(musicResponse);
    }

    @GetMapping("/find")
    public ResponseEntity<MusicResponse> loadMusic(@RequestParam(name = "song_title") String songTitle,
                                                   @RequestParam(name = "artist") String artist) {

        var music = musicService.findOneSong(songTitle, artist);
        var musicResponse = modelMapper.map(music, MusicResponse.class);

        return ResponseEntity.ok(musicResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MusicsResponse>> queryMusics(@RequestParam(name = "song_title") String songTitle,
                                                            @RequestParam(name = "artist", required = false) String artist) {
        var musics = musicService.filterMusics(songTitle, artist);
        var musicsResponse = musics
                .stream()
                .map(music -> modelMapper.map(music, MusicsResponse.class))
                .toList();

        return ResponseEntity.ok(musicsResponse);
    }

    @GetMapping("/findBy")
    public ResponseEntity<List<MusicsResponse>> findMusicBy(@RequestParam(name = "album", required = false) String album,
                                                            @RequestParam(name = "produced_by", required = false) String producedBy,
                                                            @RequestParam(name = "released_in", required = false) String releasedIn,
                                                            @RequestParam(name = "written_by", required = false) String writtenBy) {

        var fields = new HashMap<String, String>();
        fields.put("Album", album);
        fields.put("ProducedBy", producedBy);
        fields.put("ReleasedIn", releasedIn);
        fields.put("WrittenBy", writtenBy);

        var musics = musicService.findBy(fields);
        var musicsResponse = musics
                .stream()
                .map(music -> modelMapper.map(music, MusicsResponse.class))
                .toList();

        return ResponseEntity.ok(musicsResponse);
    }

}

