package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;


public interface ArtistRepository extends CrudRepository<Artist, Long> {
	
	public boolean existsByNameAndAge(String name, Integer age);

	public List<Artist> findByName(String name);
	
	@Query(value = "SELECT a FROM Artist a WHERE :movie NOT MEMBER OF a.moviesActed")		// MEMBER OF if the COLLECTION IS INSIDE THE ENTITY "IN" if its a random collection
	public List<Artist> findArtistsThatDidntActInMovie(@Param("movie") Movie movieActed);

	
}
