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
import it.uniroma3.siw.model.validator.MovieValidator;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.tool.AuthUtil;
import it.uniroma3.siw.tool.FileUploadUtil;

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

	// @GetMapping("/")
	// public String Main(Model model) {
	// model.addAttribute("movies", movieRepository.findAll());
	// return "index.html";
	// }

	// @GetMapping("/index")
	// public String indexMovies(Model model) {
	// model.addAttribute("movies", movieRepository.findAll());
	// return "index.html";
	// }

	/******************************************************************************/
	/******************************** FOR ADMINS **********************************/
	/******************************************************************************/

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
			return "admin/movie.html"; // link passato se oper. effetuata (necessita funzione get di questo link)
		} else {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError oe : errors)
				System.out.println(oe);
			return "admin/formNewMovie.html"; // link passato in caso di errore
		}

	}

	@GetMapping("/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping("/manageMovie/{id}")
	public String manageMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/manageMovie.html";
	}

	@GetMapping("/setDirectorToMovie/{movieId}/{artistId}") // id presi dal template che chiama il metodo
	public String setDirectorToMovie(@PathVariable("movieId") Long movieId, @PathVariable("artistId") Long artistId,
			Model model) {
		Movie movie = movieRepository.findById(movieId).get();
		Artist director = artistRepository.findById(artistId).get();
		movie.setDirector(director);
		movieRepository.save(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artist", director);

		return "admin/manageMovie.html";
	}

	@GetMapping("/changeMovieDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		model.addAttribute("artists", artistRepository.findAll());
		return "admin/changeMovieDirector.html";
	}

	@GetMapping("/changeActors/{movieId}")
	public String modifyArtists(@PathVariable("movieId") Long movieId, Model model) {
		Movie movie = movieRepository.findById(movieId).get();

		List<Artist> notArtists = artistRepository.findArtistsThatDidntActInMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("artistList", movie.getArtists());
		model.addAttribute("notArtistList", notArtists);

		return "admin/changeMovieActors.html";
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

		return "admin/changeMovieActors.html";
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

		return "admin/changeMovieActors.html";
	}

	/******************************************************************************/
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@PostMapping("/searchMovie")
	public String searchMovies(Model model, @RequestParam Year year) {
		model.addAttribute("movies", movieRepository.findByYear(year));
		return AuthUtil.parseLink("movies.html");
	}

	@GetMapping("/formSearchMovies")
	public String formSearchMovies() {
		return AuthUtil.parseLink("formSearchMovies.html");
	}

	@GetMapping("/movies")
	public String showMovies(Model model) {
		model.addAttribute("movies", movieRepository.findAll());

		return AuthUtil.parseLink("movies.html");
	}

	@GetMapping("/movies/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = movieRepository.findById(id).get();
		model.addAttribute("movie", movie);
		model.addAttribute("review", new Review());
		model.addAttribute("reviews", reviewRepository.findReviewsByMovie(movie));

		return AuthUtil.parseLink("movie.html");
	}

}
