package it.uniroma3.siw.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.validator.MovieValidator;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class MovieController {

	@Autowired
	MovieService movieService;

	@Autowired
	ArtistService artistService;

	@Autowired
	ReviewService reviewService;

	@Autowired
	MovieValidator movieValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields("title", "year"); // specifies which input parameters get binded to model

		binder.registerCustomEditor(Year.class, new PropertyEditorSupport() {
			public void setAsText(String value) {
				try {
					setValue(Year.parse(value));
				} catch (ParseException | DateTimeException e) {
					setValue(null);
				}
			}
		});
	}

	/******************************************************************************/
	/******************************** FOR ADMINS **********************************/
	/******************************************************************************/

	@PostMapping("/addMovie") // nome funzione chiamata nel template
	public String addMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult,
			Model model, @RequestParam("image") MultipartFile file) throws IOException { // ordine dei elem e importante

		movieValidator.validate(movie, bindingResult); // verifica errori nei campi inseriti
		if (!bindingResult.hasErrors()) {
			movieService.addMovie(movie, file);
			model.addAttribute("movie", movie);
			model.addAttribute("review", new Review());
			return "admin/movie.html"; // link passato se oper. effetuata (necessita funzione get di questo link)
		}
		return "admin/formNewMovie.html"; // link passato in caso di errore
	}

	@GetMapping("/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping("/removeMovie/{id}")
	public String removeMovie(@PathVariable("id") Long id, Model model) {
		List<Movie> movies = movieService.removeMovie(id);

		model.addAttribute("movies", movies);
		return "admin/movies.html";
	}

	@GetMapping("/manageMovie/{id}")
	public String manageMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieService.findById(id));
		return "admin/manageMovie.html";
	}

	@PostMapping("/modifyMovie/{movieId}")
	public String modifyMovie(Model model, @PathVariable("movieId") Long movieId,
			@RequestParam(value = "titolo", required = false) String titolo,
			@RequestParam(value = "anno", required = false) Year anno,
			@RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

		Movie movie = movieService.modifyMovie(movieId, titolo, anno, file);
		model.addAttribute("movie", movie);
		return "admin/manageMovie.html";
	}

	@GetMapping("/setDirectorToMovie/{movieId}/{artistId}") // id presi dal template che chiama il metodo
	public String setDirectorToMovie(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieService.setDirectorToMovie(movieId, artistId);

		model.addAttribute("movie", movie);
		model.addAttribute("artist", movie.getDirector());
		return "admin/manageMovie.html";
	}

	@GetMapping("/changeMovieDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieService.findById(id));
		model.addAttribute("artists", artistService.findAll());
		return "admin/changeMovieDirector.html";
	}

	@GetMapping("/changeActors/{movieId}")
	public String changeActors(@PathVariable("movieId") Long movieId, Model model) {
		Movie movie = movieService.findById(movieId);
		List<Artist> notArtists = artistService.findArtistsNotInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);
		return "admin/changeMovieActors.html";
	}

	@GetMapping("/addActor/{movieId}/{artistId}")
	public String addActor(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieService.addActorToMovie(artistId, movieId);
		List<Artist> notArtists = artistService.findArtistsNotInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);
		return "admin/changeMovieActors.html";
	}

	@GetMapping("/removeActor/{movieId}/{artistId}")
	public String removeArtist(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieService.removeActorFromMovie(artistId, movieId);
		List<Artist> notArtists = artistService.findArtistsNotInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);
		return "admin/changeMovieActors.html";
	}

	/******************************************************************************/
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@PostMapping("/searchMovie")
	public String searchMovies(Model model, @RequestParam Year year) {
		model.addAttribute("movies", movieService.findByYear(year));
		return AuthUtil.parseLink("movies.html");
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return AuthUtil.parseLink("formSearchMovies.html");
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies", movieService.findAll());
		return AuthUtil.parseLink("movies.html");
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = movieService.findById(id);

		model.addAttribute("movie", movie);
		model.addAttribute("review", new Review());
		model.addAttribute("reviews", reviewService.findReviewsByMovie(movie));
		return AuthUtil.parseLink("movie.html");
	}

}
