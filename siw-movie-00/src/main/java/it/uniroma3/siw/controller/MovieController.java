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
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.upload.FileUploadUtil;
import it.uniroma3.siw.validator.MovieValidator;

@Controller
public class MovieController {

	@Autowired
	MovieRepository movieRepository; // gli id dei movie nel repo sono creati automaticamente
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	MovieValidator movieValidator;
	// @Autowired
	// private ObjectMapper objectMapper;

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

			public String getAsText(Year year) {
				return year.toString();
			}
		});
	}

	@PostMapping("/searchMovie")
	public String searchMovies(Model model, @RequestParam Year year) {
		model.addAttribute("movies", movieRepository.findByYear(year));
		return "manageMovies.html";
	}

	@PostMapping("/addMovie") // nome funzione chiamata nel template
	public String addMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult,
			Model model, @RequestParam("image") MultipartFile file) throws IOException { // ordine dei elem e importante

		movieValidator.validate(movie, bindingResult); // verifica errori nei campi inseriti

		if (!bindingResult.hasErrors()) {
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			movie.setImage(fileName);
			Movie savedMovie = movieRepository.save(movie); // salva il film per ricevere un id
			String uploadDir = Movie.IMAGE_PATH + "/" + savedMovie.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, file);

			model.addAttribute("review", new Review());
			model.addAttribute("movie", movie);
			return "movie.html"; // link passato se oper. effetuata (necessita funzione get di questo link)
		} else {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError oe : errors)
				System.out.println(oe);
			return "formNewMovie.html"; // link passato in caso di errore
		}

	}

	// @PostMapping("/updateImage")
	// public String updateImage(Movie movie, Model model, @RequestParam("image")
	// MultipartFile file) throws IOException {

	// if (movieRepository.existsById(movie.getId())) {
	// String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	// // Movie movie = movieRepository.findById(id).get();
	// movie.setImage(fileName);
	// movieRepository.updateMovieImageById(fileName, movie.getId());
	// String uploadDir = Movie.IMAGE_PATH + "/" + movie.getId();
	// FileUploadUtil.saveFile(uploadDir, fileName, file);

	// model.addAttribute("movie", movie);
	// }

	// return "updateMovie.html";

	// }

	// @PatchMapping("/updateImage/{id}")
	// ResponseEntity<?> updateImage(@RequestBody Map<String, String> movie) {
	// Movie toBePatchedMovie = objectMapper.convertValue(movie, Movie.class);
	// movieRepository.patch(toBePatchedMovie);
	// }

	@GetMapping("/")
	public String Main(Model model) {
		model.addAttribute("movies", movieRepository.findAll());
		return "index.html";
	}

	@GetMapping("/index")
	public String indexMovies(Model model) {
		model.addAttribute("movies", movieRepository.findAll());
		return "index.html";
	}

	@GetMapping("/manageMovies")
	public String showMovies(Model model) {
		model.addAttribute("movies", movieRepository.findAll());
		return "manageMovies.html";
	}

	@GetMapping("/manageMovies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		model.addAttribute("review", new Review());
		model.addAttribute("reviews", reviewRepository.findReviewsByMovie(movie));
		return "movie.html";
	}

	// @GetMapping("/searchMovie/{year}")
	// public String showMoviesByYear(@PathVariable("year") Year year, Model model)
	// {
	// // model.addAttribute("movie", movieRepository.findById(id).get());
	// // return "movie.html";
	// model.addAttribute("movies", movieRepository.findByYear(year));
	// return "manageMovies.html";
	// }

	@GetMapping("/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "formNewMovie.html";
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return "formSearchMovies.html";
	}

	@GetMapping("/showArtists")
	public String showArtists(Model model) {
		model.addAttribute("artists", artistRepository.findAll());
		return "showArtists.html";
	}

	@GetMapping("/updateMovie/{id}")
	public String updateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "updateMovie.html";
	}

	@GetMapping("/addDirectorToMovie/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		model.addAttribute("artists", artistRepository.findAll());
		return "addDirector.html";
	}

	@GetMapping("/setDirectorToMovie/{movieId}/{artistId}") // gli id vengono presi dalla pagina che chiama la funzione
															// e sono da passare alla prossima pagina
	public String setDirectorToMovie(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieRepository.findById(movieId).get();
		Artist director = artistRepository.findById(artistId).get();
		movie.setDirector(director);
		movieRepository.save(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artist", director);

		return "updateMovie.html";
	}

	@GetMapping("/changeActors/{movieId}")
	public String modifyArtists(@PathVariable("movieId") Long movieId, Model model) {
		Movie movie = movieRepository.findById(movieId).get();

		List<Artist> notArtists = artistRepository.findArtistsThatDidntActInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);

		return "addActors.html";
	}

	@GetMapping("/addActor/{movieId}/{artistId}")
	public String addArtist(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieRepository.findById(movieId).get();
		Artist artist = artistRepository.findById(artistId).get();

		movie.getArtists().add(artist);
		artist.getMoviesActed().add(movie);

		movieRepository.save(movie);
		artistRepository.save(artist);

		List<Artist> notArtists = artistRepository.findArtistsThatDidntActInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);

		return "addActors.html";
	}

	@GetMapping("/removeActor/{movieId}/{artistId}")
	public String removeArtist(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieRepository.findById(movieId).get();
		Artist artist = artistRepository.findById(artistId).get();

		movie.getArtists().remove(artist);
		artist.getMoviesActed().remove(movie);

		movieRepository.save(movie);
		artistRepository.save(artist);

		List<Artist> notArtists = artistRepository.findArtistsThatDidntActInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);

		return "addActors.html";
	}

}
