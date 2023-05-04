package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long> {
    
    @Query(value = "SELECT r FROM Review r WHERE r.movie=:movie")
	List<Review> findReviewsByMovie(@Param("movie") Movie movieReviewed);
}