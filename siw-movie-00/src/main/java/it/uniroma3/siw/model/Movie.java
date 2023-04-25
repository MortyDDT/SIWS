package it.uniroma3.siw.model;

import java.time.Year;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;

import org.springframework.data.annotation.Transient;



@Entity
public class Movie {
	
	public static final String IMAGE_PATH = "src/main/resources/movie-images";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotBlank
	private String title;
	
	@PastOrPresent
	private Year year;
	
	@Column(nullable = true, length = 64)
    private String image;
		
	@ManyToOne
	private Artist director;
	
	@ManyToMany(mappedBy = "moviesActed")
	private List<Artist> artists;
	
	
	@Transient
    public String getImagePath() {
        if (image == null || id == null) 
        	return null;
        return "/" + IMAGE_PATH + "/" + id + "/" + image;
    }
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Year getYear() {
		return year;
	}
	public void setYear(Year year) {
		this.year = year;
	}
	public Artist getDirector() {
		return director;
	}
	public void setDirector(Artist director) {
		this.director = director;
	}
	public List<Artist> getArtists() {
		return artists;
	}
	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		return Objects.equals(title, other.title) && Objects.equals(year, other.year);
	}

}