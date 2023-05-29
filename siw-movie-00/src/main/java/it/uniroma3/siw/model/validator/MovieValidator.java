package it.uniroma3.siw.model.validator;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.MovieService;

@Component
public class MovieValidator implements Validator {

	private static final int ANNO_PRIMO_FILM = 1878;

	@Autowired
	private MovieService movieService;

	@Override
	public void validate(Object o, Errors errors) {
		Movie movie = (Movie) o;
		String title = movie.getTitle();
		Year year = movie.getYear();

		if ((title != null && year != null) && movieService.existsByTitleAndYear(title, year))
			errors.reject("movie.duplicate");

		if (year != null && year.getValue() < ANNO_PRIMO_FILM)
			errors.reject("movie.minyear");

	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Movie.class.equals(aClass);
	}
}
