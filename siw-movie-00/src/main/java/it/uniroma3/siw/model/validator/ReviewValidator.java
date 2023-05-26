package it.uniroma3.siw.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.tool.AuthUtil;

@Component
public class ReviewValidator implements Validator{
    
    @Autowired
	private ReviewService reviewService;

	@Override
	public void validate(Object o, Errors errors) {
		Review review = (Review) o;
		Movie movie = review.getMovie();
		User user = review.getUser();

		if ((movie != null && user != null) && reviewService.existsByUserAndMovie(user, movie))
			errors.reject("review.duplicate");
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Review.class.equals(aClass);
	}

}
