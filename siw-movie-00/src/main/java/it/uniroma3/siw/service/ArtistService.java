package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.tool.FileUploadUtil;

@Service
public class ArtistService {

    @Autowired
    protected ArtistRepository artistRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public List<Artist> addArtist(Artist artist, MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        artist.setImageName(fileName);
        Artist savedArtist = artistRepository.save(artist);
        String uploadDir = Artist.IMAGE_PATH + "/" + savedArtist.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, file);

        List<Artist> artists = new ArrayList<>();
        Iterable<Artist> iterable = artistRepository.findAll();
        for (Artist a : iterable)
            artists.add(a);

        return artists;
    }

    @Transactional
    public Artist modifyArtist(Long artistId, String name, String surname, LocalDate birthDate, LocalDate deathDate,
            MultipartFile file) throws IOException {

        Artist artist = artistRepository.findById(artistId).get();

        if (!(name.isEmpty() || name.isBlank())) // in spring 2 empty fields get parsed as ""
            artist.setName(name);

        if (!(surname.isEmpty() || surname.isBlank()))
            artist.setSurname(surname);

        if (birthDate != null)
            artist.setBirthDate(birthDate);

        if (deathDate != null)
            artist.setDeathDate(deathDate);

        if (file.getSize() > 0) { // in spring 2 isEmpty doesen't work
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            artist.setImageName(fileName);
            String uploadDir = Artist.IMAGE_PATH + "/" + artist.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        return artistRepository.save(artist);
    }

    @Transactional // reviews get deleted automatically by cascade type remove in movie
    public List<Artist> removeArtist(Long id) {
        Artist artist = artistRepository.findById(id).get();
        Collection<Movie> movies = artist.getMoviesActed();

        for (Movie movie : movies){
            if(movie.getDirector().equals(artist))
                movie.setDirector(null);
            movie.getArtists().remove(artist);
        }

        artistRepository.delete(artist);

        List<Artist> artists = new ArrayList<>();
        Iterable<Artist> iterable = artistRepository.findAll();
        for (Artist a : iterable)
            artists.add(a);

        return artists;
    }

    /******************************************************************************/
    /********************************** GENERAL ***********************************/
    /******************************************************************************/

    @Transactional
    public Artist findById(Long id) {
        return artistRepository.findById(id).get();
    }

    @Transactional
    public List<Artist> findAll() {
        List<Artist> artists = new ArrayList<>();
        Iterable<Artist> iterable = artistRepository.findAll();
        for (Artist artist : iterable)
            artists.add(artist);

        return artists;
    }

    @Transactional
    public List<Artist> findArtistsNotInMovie(Movie movie) {
        List<Artist> artists = new ArrayList<>();
        Iterable<Artist> iterable = artistRepository.findArtistsThatDidntActInMovie(movie);
        for (Artist artist : iterable)
            artists.add(artist);

        return artists;
    }

    @Transactional
    public boolean existsByNameAndSurname(String name, String surname) {
        return artistRepository.existsByNameAndSurname(name, surname);
    }

}
