package it.uniroma3.siw.repository;
import java.time.Year;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;


public interface MovieRepository extends CrudRepository<Movie, Long> {
	public List<Movie> findByYear(Year year);
	public boolean existsByTitleAndYear(String title, Year year);
	
	@Modifying
	@Query("update Movie m set m.image = ?1 where m.id = ?2")
	void updateMovieImageById(String imageName, Long movieId);
}
