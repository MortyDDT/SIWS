package it.uniroma3.siw.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.validator.ReviewValidator;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class ReviewController {

	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	UserService userService;
	@Autowired
	CredentialsService credentialsService;
	@Autowired
	ReviewValidator reviewValidator;

	/******************************************************************************/
	/******************************* AUTHENTICATED ********************************/
	/******************************************************************************/

	@PostMapping("/addReview/{movieId}")
	public String addReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model,
			@PathVariable("movieId") Long movieId) {
		reviewValidator.validate(review, bindingResult);

		if (!bindingResult.hasErrors()) {
			String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.getUsername();
			Credentials credenzialiAttuali = credentialsService.getCredentials(username);
			User utenteAttuale = credenzialiAttuali.getUser();
			Movie movie = movieRepository.findById(movieId).get();

			movie.getReviews().add(review);
			utenteAttuale.getReviews().add(review);
			review.setMovie(movie);
			review.setUser(utenteAttuale);

			userService.saveUser(utenteAttuale);
			reviewRepository.save(review);
			movieRepository.save(movie);

			model.addAttribute("movie", movie);
			model.addAttribute("reviews", reviewRepository.findReviewsByMovie(movie));
		} else {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError oe : errors)
				System.out.println(oe);
		}
		return AuthUtil.parseLink("movie.html");
	}

}
