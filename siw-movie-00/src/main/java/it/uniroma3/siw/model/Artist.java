package it.uniroma3.siw.model;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "artists")
public class Artist {

	public static final String IMAGE_PATH = "src/main/resources/static/images/actor-images";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String name;
	@NotBlank
	private String surname;

	@NotNull
	@DateTimeFormat(pattern="dd/MM/yyyy") 
	private LocalDate birthDate;

	@DateTimeFormat(pattern="dd/MM/yyyy") 
	private LocalDate deathDate;

	@Column(nullable = true, length = 64)
    private String imageName;

	@OneToMany(mappedBy = "director")
	private List<Movie> moviesDirected;

	@ManyToMany(cascade = {CascadeType.PERSIST})
	private List<Movie> moviesActed;


	@Transient
    public String getImagePath() {
        if (imageName == null || id == null) 
        	return null;
		// need the relative path since in authConfig only /images/** is authorized not /src...
		String relative_path = IMAGE_PATH.substring(25);
        return relative_path + "/" + id + "/" + imageName;
    }


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


	public String getSurname() {
		return surname;
	}


	public void setSurname(String surname) {
		this.surname = surname;
	}


	public LocalDate getBirthDate() {
		return birthDate;
	}


	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}


	public LocalDate getDeathDate() {
		return deathDate;
	}


	public void setDeathDate(LocalDate deathDate) {
		this.deathDate = deathDate;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}


	public List<Movie> getMoviesDirected() {
		return moviesDirected;
	}


	public void setMoviesDirected(List<Movie> moviesDirected) {
		this.moviesDirected = moviesDirected;
	}


	public List<Movie> getMoviesActed() {
		return moviesActed;
	}


	public void setMoviesActed(List<Movie> moviesActed) {
		this.moviesActed = moviesActed;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		return result;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}


}
