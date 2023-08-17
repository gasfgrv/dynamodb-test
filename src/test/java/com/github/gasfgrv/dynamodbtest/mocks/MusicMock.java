package com.github.gasfgrv.dynamodbtest.mocks;

import com.github.gasfgrv.dynamodbtest.domain.model.MusicEntity;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MusicMock {

    public MusicEntity getMusic() {
        var music = new MusicEntity();
        music.setSongTitle("Falso Realismo");
        music.setArtist("Jambu");
        music.setWrittenBy(List.of("Gabriel Mar Dantas"));
        music.setProducedBy(List.of("Roberto Freire da Silva Rôla", "Yasmin Moura Costa", "Gustavo da Costa Pessoa"));
        music.setAlbum("Falso Realismo");
        music.setReleasedIn(2021);

        return music;
    }

    public List<MusicEntity> getMusics() {
        var music1 = new MusicEntity();
        music1.setSongTitle("Ain't No Sunshine");
        music1.setArtist("Bill Withers");
        music1.setWrittenBy(List.of("Bill Withers"));
        music1.setProducedBy(List.of("Booker T. Jones"));
        music1.setAlbum("Just As I Am");
        music1.setReleasedIn(1971);

        var music2 = new MusicEntity();
        music2.setSongTitle("Ain't No Sunshine");
        music2.setArtist("Lighthouse Family");
        music2.setWrittenBy(List.of("Bill Withers"));
        music2.setProducedBy(List.of("Paul Tucker", "Tim Laws"));
        music2.setAlbum("Greatest Hits");
        music2.setReleasedIn(2002);

        return List.of(music1, music2);
    }

}
