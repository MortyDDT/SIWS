package it.uniroma3.siw.model;

import java.time.Year;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "movies")
public class Movie {

	public static final String IMAGE_PATH = "src/main/resources/static/images/movie-images";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	private String title;

	@NotNull
	@PastOrPresent
	private Year year;

	@Column(nullable = true, length = 64)
	private String imageName;

	@ManyToOne
	private Artist director;

	@ManyToMany(mappedBy = "moviesActed", cascade = {CascadeType.PERSIST})
	private List<Artist> artists;

	@OneToMany(mappedBy = "movie", cascade = {CascadeType.REMOVE})
	private List<Review> reviews;

	@Transient
	public String getImagePath() {
		if (imageName == null || imageName.isBlank())
			return null;
		// need relative path since in authConfig only /images/** is authorized not /src..
		String relative_path = IMAGE_PATH.substring(25);
		return relative_path + "/" + id + "/" + imageName;
	}

	@Transient
	public Double getAverageScore() {
		
		if (reviews != null && !reviews.isEmpty()) {
			Integer sum = 0;
			Collection<Integer> voti = new LinkedList<Integer>();

			for (Review review : reviews)
				voti.add(review.getScore());
			for (Integer voto : voti)
				sum += voto;
			return sum.doubleValue() / voti.size();
		}
		return 0.0;
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

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String image) {
		this.imageName = image;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
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