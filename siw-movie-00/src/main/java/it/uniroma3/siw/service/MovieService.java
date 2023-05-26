package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.Year;
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
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.tool.FileUploadUtil;

@Service
public class MovieService {

    @Autowired
    protected MovieRepository movieRepository;

    @Autowired
    protected ArtistRepository artistRepository;

    @Autowired
    protected ReviewRepository reviewRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public void addMovie(Movie movie, MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        movie.setImageName(fileName);
        Movie savedMovie = movieRepository.save(movie);
        String uploadDir = Movie.IMAGE_PATH + "/" + savedMovie.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, file);
    }

    @Transactional
    public Movie setDirectorToMovie(Long movieId, Long artistId) {
        Movie movie = movieRepository.findById(movieId).get();
        Artist director = artistRepository.findById(artistId).get();
        movie.setDirector(director);
        movieRepository.save(movie);

        return movie;
    }

    @Transactional
    public Movie addActorToMovie(Long artistId, Long movieId) {
        Movie movie = movieRepository.findById(movieId).get();
        Artist artist = artistRepository.findById(artistId).get();

        movie.getArtists().add(artist);
        artist.getMoviesActed().add(movie);

        movieRepository.save(movie);
        artistRepository.save(artist);

        return movie;
    }

    @Transactional
    public Movie removeActorFromMovie(Long artistId, Long movieId) {
        Movie movie = movieRepository.findById(movieId).get();
        Artist artist = artistRepository.findById(artistId).get();

        movie.getArtists().remove(artist);
        artist.getMoviesActed().remove(movie);

        movieRepository.save(movie);
        artistRepository.save(artist);

        return movie;
    }

    @Transactional
    public Movie modifyMovie(Long movieId, String title, Year year, MultipartFile file) throws IOException {

        Movie movie = movieRepository.findById(movieId).get();

        if (!(title.isEmpty() || title.isBlank())) // in spring 2 empty fields get parsed as ""
            movie.setTitle(title);

        if (year != null)
            movie.setYear(year);

        if (file.getSize() > 0) { // in spring 2 isEmpty doesen't work
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            movie.setImageName(fileName);
            String uploadDir = Movie.IMAGE_PATH + "/" + movie.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        return movieRepository.save(movie);
    }

    @Transactional // reviews get deleted automatically by cascade type remove in movie
    public List<Movie> removeMovie(Long id) {
        Movie movie = movieRepository.findById(id).get();
        Collection<Artist> artists = movie.getArtists();

        for (Artist artist : artists)
            artist.getMoviesActed().remove(movie); // artists get saved automatically cuz of cascade type

        movieRepository.delete(movie);

        List<Movie> movies = new ArrayList<>();
        Iterable<Movie> iterable = movieRepository.findAll();
        for (Movie m : iterable)
            movies.add(m);

        return movies;
    }

    /******************************************************************************/
    /********************************** GENERAL ***********************************/
    /******************************************************************************/

    @Transactional
    public Movie findById(Long id) {
        return movieRepository.findById(id).get();
    }

    @Transactional
    public List<Movie> findByYear(Year year) {
        return movieRepository.findByYear(year);
    }

    @Transactional
    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        Iterable<Movie> iterable = movieRepository.findAll();
        for (Movie movie : iterable)
            movies.add(movie);

        return movies;
    }

    @Transactional
    public List<Movie> findByTitleContaining(String substring) {
        List<Movie> movies = new ArrayList<>();
        Iterable<Movie> iterable = movieRepository.findByTitleContainingIgnoreCase(substring);
        for (Movie movie : iterable)
            movies.add(movie);

        return movies;
    }

}
