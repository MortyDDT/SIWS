package it.uniroma3.siw.siwsocial00.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

import org.springframework.data.annotation.Transient;

@Entity
@Table(name = "stories")
public class Story {

   public static final String IMAGE_PATH = "src/main/resources/static/images/story-images";

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;

   @NotBlank
   private String description;

   private Integer likes;

   private LocalDateTime dateAdded;

   @Column(nullable = true, length = 64)
   private String imageName;

   @ManyToOne
   private User author;

   @OneToMany(mappedBy = "story", cascade = { CascadeType.REMOVE })
   private List<Comment> comments;

   @ManyToMany
   private List<User> usersThatLiked;

   @Transient
   public String getImagePath() {
      if (imageName.isBlank() || imageName == null || id == null)
         return null;
      String relative_path = IMAGE_PATH.substring(25);
      return relative_path + "/" + id + "/" + imageName;
   }

   @Transient
   public void addLike() {
      likes++;
   }

   @Transient
   public void removeLike() {
      if (likes > 0)
         likes--;
   }

   @Transient
   public String getDateParsed() {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
      return dateAdded.format(dateFormat);
   }

   public List<User> getUsersThatLiked() {
      return usersThatLiked;
   }

   public void setUsersThatLiked(List<User> usersThatLiked) {
      this.usersThatLiked = usersThatLiked;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Integer getLikes() {
      return likes;
   }

   public void setLikes(Integer likes) {
      this.likes = likes;
   }

   public LocalDateTime getDateAdded() {
      return dateAdded;
   }

   public void setDateAdded(LocalDateTime dateAdded) {
      this.dateAdded = dateAdded;
   }

   public String getImageName() {
      return imageName;
   }

   public void setImageName(String imageName) {
      this.imageName = imageName;
   }

   public User getAuthor() {
      return author;
   }

   public void setAuthor(User author) {
      this.author = author;
   }

   public List<Comment> getComments() {
      return comments;
   }

   public void setComments(List<Comment> comments) {
      this.comments = comments;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((dateAdded == null) ? 0 : dateAdded.hashCode());
      result = prime * result + ((author == null) ? 0 : author.hashCode());
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
      Story other = (Story) obj;
      if (dateAdded == null) {
         if (other.dateAdded != null)
            return false;
      } else if (!dateAdded.equals(other.dateAdded))
         return false;
      if (author == null) {
         if (other.author != null)
            return false;
      } else if (!author.equals(other.author))
         return false;
      return true;
   }

}
