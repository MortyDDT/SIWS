package it.uniroma3.siw.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.validator.ReviewValidator;

@Controller
public class ReviewController {
    
    @Autowired
	ReviewRepository reviewRepository;
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	ReviewValidator reviewValidator;

    @PostMapping("/addReview/{movieId}")
	public String addReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, Model model, @PathVariable("movieId") Long movieId) {		
		reviewValidator.validate(review, bindingResult);

		if (!bindingResult.hasErrors()) {
			Movie movie = movieRepository.findById(movieId).get();
			movie.getReviews().add(review);
			review.setMovie(movie);

			reviewRepository.save(review);
			movieRepository.save(movie);
			
			model.addAttribute("movie", movie);
			model.addAttribute("reviews", reviewRepository.findReviewsByMovie(movie));
		} else {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError oe : errors)
				System.out.println(oe);			
		}
        return "movie.html";
	}

}
