package it.uniroma3.siw.validator;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;

@Component
public class MovieValidator implements Validator {
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Movie movie = (Movie) o;
		
		if(movie.getTitle() == null)
			errors.reject("NotBlank.movie.title");
		
		if(movie.getYear() == null)
			errors.reject("NotBlank.movie.year");
		
		
		if (movie.getTitle() != null && movie.getYear() != null
				&& movieRepository.existsByTitleAndYear(movie.getTitle(), movie.getYear())) {
			errors.reject("movie.duplicate");
		}
		
		if(movie.getYear() != null) {
			if(movie.getYear().getClass() != Year.class)
				errors.reject("typeMismatch.java.lang.Integer");
			
			if(movie.getYear().getValue() < 1878)
				errors.reject("Min.year");
			
			if(movie.getYear().getValue() > Year.now().getValue())
				errors.reject("Max.year");
		}
		

	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Movie.class.equals(aClass);
	}
}
