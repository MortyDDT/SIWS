package it.uniroma3.siw.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.validator.ReviewValidator;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class ReviewController {

	@Autowired
	UserService userService;

	@Autowired
	ReviewService reviewService;

	@Autowired
	ReviewValidator reviewValidator;
	
	@Autowired
	MovieService movieService;

	/******************************************************************************/
	/******************************* AUTHENTICATED ********************************/
	/******************************************************************************/

	@PostMapping("/addReview/{movieId}")
	public String addReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model,
			@PathVariable("movieId") Long movieId) {

		Movie movie = movieService.findById(movieId);
		List<Review> reviews = reviewService.findReviewsByMovie(movie);
		reviewService.addReviewSetUserAndMovie(review, movieId);

		reviewValidator.validate(review, bindingResult);
		
		if (!bindingResult.hasErrors())
			reviews = reviewService.addReviewAndSave(review);			

		model.addAttribute("reviews", reviews);
		model.addAttribute("movie", movie);
		return AuthUtil.parseLink("movie.html");
	}

	@GetMapping("/removeReview/{id}")
	public String removeReview(@PathVariable("id") Long id, Model model) {
		
		Review review = reviewService.findById(id);
		Movie movie = review.getMovie();
		
		reviewService.removeReview(review);

		List<Review> reviews = reviewService.findReviewsByMovie(movie);

		model.addAttribute("movie", movie);
		model.addAttribute("review", new Review());
		model.addAttribute("reviews", reviews);


		return "admin/movie.html";
	}


}
