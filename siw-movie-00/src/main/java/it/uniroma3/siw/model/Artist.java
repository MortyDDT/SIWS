package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "artists")
public class Artist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String name;
	
	@Min(0)
	@Max(100)
	@NotNull
	private Integer age;

	

	@OneToMany(mappedBy = "director")
	private List<Movie> moviesDirected;

	@ManyToMany
	private List<Movie> moviesActed;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}

	public List<Movie> getFilmRegizati() {
		return moviesDirected;
	}
	public void setFilmRegizati(List<Movie> filmRegizati) {
		this.moviesDirected = filmRegizati;
	}

	public List<Movie> getMoviesActed() {
		return moviesActed;
	}
	public void setMoviesActed(List<Movie> moviesActed) {
		this.moviesActed = moviesActed;
	}
	
	public List<Movie> getMoviesDirected() {
		return moviesDirected;
	}
	public void setMoviesDirected(List<Movie> moviesDirected) {
		this.moviesDirected = moviesDirected;
	}


	@Override
	public int hashCode() {
		return Objects.hash(age, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artist other = (Artist) obj;
		return Objects.equals(age, other.age) && Objects.equals(name, other.name);
	}

}
