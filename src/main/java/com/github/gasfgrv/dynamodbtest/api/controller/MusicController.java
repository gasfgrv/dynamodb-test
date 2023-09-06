package com.github.gasfgrv.dynamodbtest.api.controller;

import com.github.gasfgrv.dynamodbtest.api.model.MusicRequest;
import com.github.gasfgrv.dynamodbtest.api.model.MusicResponse;
import com.github.gasfgrv.dynamodbtest.api.model.MusicsResponse;
import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import com.github.gasfgrv.dynamodbtest.domain.service.MusicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
@Tag(name = "Music")
public class MusicController {

    private static final String MOUNTING_THE_RESPONSE_BODY = "Mounting the response body";
    private static final String GATHERING_RESPONSE_DATA = "Gathering response data";
    private static final String RECEIVED_REQUEST = "Received request: [{}] {}";

    private final MusicService musicService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<MusicResponse> saveMusic(@RequestBody @Valid MusicRequest request,
                                                   HttpServletRequest httpRequest) {
        log.info(RECEIVED_REQUEST, httpRequest.getMethod(), httpRequest.getServletPath());
        var entity = modelMapper.map(request, MusicEntity.class);
        var savedMusic = musicService.addASong(entity);

        log.info("Mounting the 'Location' header in response");
        var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/find")
                .queryParam("song_title", savedMusic.getSongTitle())
                .queryParam("artist", savedMusic.getArtist())
                .build()
                .toUri();

        log.info(MOUNTING_THE_RESPONSE_BODY);
        var musicResponse = modelMapper.map(savedMusic, MusicResponse.class);

        log.info(GATHERING_RESPONSE_DATA);
        return ResponseEntity.created(location).body(musicResponse);
    }

    @GetMapping("/find")
    public ResponseEntity<MusicResponse> loadMusic(@RequestParam(name = "song_title") String songTitle,
                                                   @RequestParam(name = "artist") String artist,
                                                   HttpServletRequest httpRequest) {

        log.info(RECEIVED_REQUEST.concat("?{}"),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                httpRequest.getQueryString());

        var music = musicService.findOneSong(songTitle, artist);

        log.info(MOUNTING_THE_RESPONSE_BODY);
        var musicResponse = modelMapper.map(music, MusicResponse.class);

        log.info(GATHERING_RESPONSE_DATA);
        return ResponseEntity.ok(musicResponse);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MusicsResponse>> queryMusics(@RequestParam(name = "song_title") String songTitle,
                                                            @RequestParam(name = "artist", required = false) String artist,
                                                            HttpServletRequest httpRequest) {

        log.info(RECEIVED_REQUEST.concat("?{}"),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                httpRequest.getQueryString());

        var musics = musicService.filterMusics(songTitle, artist);
        var musicsResponse = musics
                .stream()
                .map(music -> {
                    log.info(MOUNTING_THE_RESPONSE_BODY);
                    return modelMapper.map(music, MusicsResponse.class);
                })
                .toList();

        log.info(GATHERING_RESPONSE_DATA);
        return ResponseEntity.ok(musicsResponse);
    }

    @GetMapping("/findBy")
    public ResponseEntity<List<MusicsResponse>> findMusicBy(@RequestParam(name = "album", required = false) String album,
                                                            @RequestParam(name = "produced_by", required = false) String producedBy,
                                                            @RequestParam(name = "released_in", required = false) String releasedIn,
                                                            @RequestParam(name = "written_by", required = false) String writtenBy,
                                                            HttpServletRequest httpRequest) {

        log.info(RECEIVED_REQUEST.concat("?{}"),
                httpRequest.getMethod(),
                httpRequest.getServletPath(),
                httpRequest.getQueryString());

        var fields = new HashMap<String, String>();
        fields.put("Album", album);
        fields.put("ProducedBy", producedBy);
        fields.put("ReleasedIn", releasedIn);
        fields.put("WrittenBy", writtenBy);

        var musics = musicService.findBy(fields);
        var musicsResponse = musics
                .stream()
                .map(music -> {
                    log.info(MOUNTING_THE_RESPONSE_BODY);
                    return modelMapper.map(music, MusicsResponse.class);
                })
                .toList();

        log.info(GATHERING_RESPONSE_DATA);
        return ResponseEntity.ok(musicsResponse);
    }

}

