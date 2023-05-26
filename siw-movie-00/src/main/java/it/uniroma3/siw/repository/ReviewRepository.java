package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    
    @Query(value = "SELECT r FROM Review r WHERE r.movie=:movie")
	public List<Review> findReviewsByMovie(@Param("movie") Movie movieReviewed);

    @Query(value = "SELECT r FROM Review r WHERE r.user=:user AND r.movie=:movie")
	public List<Review> findReviewsByUserAndMovie(@Param("user") User user ,@Param("movie") Movie movieReviewed);

    public boolean existsByUserAndMovie(User user, Movie movie);

}