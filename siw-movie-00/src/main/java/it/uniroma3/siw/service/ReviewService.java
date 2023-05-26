package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class ReviewService {

    @Autowired
    protected ReviewRepository reviewRepository;

    @Autowired
    protected MovieRepository movieRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public void addReviewSetUserAndMovie(Review review, Long movieId) {

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        Credentials credenzialiAttuali = credentialsRepository.findByUsername(username).get();
        User utenteAttuale = credenzialiAttuali.getUser();
        Movie movie = movieRepository.findById(movieId).get();

        movie.getReviews().add(review);
        utenteAttuale.getReviews().add(review);
        review.setMovie(movie);
        review.setUser(utenteAttuale);
    }
    @Transactional
    public List<Review> addReviewAndSave(Review review) {

        Movie movie = review.getMovie();
        User user = review.getUser();

        userRepository.save(user);
        movieRepository.save(movie);
        reviewRepository.save(review);

        List<Review> reviews = new ArrayList<>();
        Iterable<Review> iterable = reviewRepository.findReviewsByMovie(movie);
        for (Review r : iterable)
            reviews.add(r);

        return reviews;
    }


    @Transactional
    public List<Review> removeReview(Review review) {
        User user = review.getUser();
        Movie movie = review.getMovie();
        
        user.getReviews().remove(review);
        movie.getReviews().remove(review);

        reviewRepository.delete(review);

        List<Review> reviews = new ArrayList<>();
        Iterable<Review> iterable = reviewRepository.findReviewsByMovie(movie);
        for (Review r : iterable)
            reviews.add(r);

        return reviews;
    }

    /******************************************************************************/
    /********************************** GENERAL ***********************************/
    /******************************************************************************/

    @Transactional
    public Review findById(Long id) {
        return reviewRepository.findById(id).get();
    }

    @Transactional
    public List<Review> findAll() {
        List<Review> reviews = new ArrayList<>();
        Iterable<Review> iterable = reviewRepository.findAll();
        for (Review review : iterable)
            reviews.add(review);

        return reviews;
    }

    @Transactional
    public List<Review> findByUserAndMovie(User user, Movie movie) {
        List<Review> reviews = new ArrayList<>();
        Iterable<Review> iterable = reviewRepository.findReviewsByUserAndMovie(user, movie);
        for (Review review : iterable)
            reviews.add(review);

        return reviews;
    }

    @Transactional
    public boolean existsByUserAndMovie(User user, Movie movie) {
        return reviewRepository.existsByUserAndMovie(user, movie);
    }

    @Transactional
    public List<Review> findReviewsByMovie(Movie movie) {
        List<Review> reviews = new ArrayList<>();
        Iterable<Review> iterable = reviewRepository.findReviewsByMovie(movie);
        for (Review review : iterable)
            reviews.add(review);

        return reviews;
    }

}
