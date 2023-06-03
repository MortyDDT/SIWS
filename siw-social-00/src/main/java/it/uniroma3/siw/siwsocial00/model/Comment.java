package it.uniroma3.siw.siwsocial00.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "comments")
public class Comment {

   @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

   private String description;

   @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
   private LocalDateTime dateAdded;

   @ManyToOne
	private User author;

   @ManyToOne
	private Story story;


   @Transient
   public String getDateParsed() {
      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
      return dateAdded.format(dateFormat);
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

   public User getAuthor() {
      return author;
   }

   public void setAuthor(User author) {
      this.author = author;
   }

   public Story getStory() {
      return story;
   }

   public void setStory(Story story) {
      this.story = story;
   }

   public LocalDateTime getDateAdded() {
      return dateAdded;
   }

   public void setDateAdded(LocalDateTime dateAdded) {
      this.dateAdded = dateAdded;
   }
   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((description == null) ? 0 : description.hashCode());
      result = prime * result + ((author == null) ? 0 : author.hashCode());
      result = prime * result + ((story == null) ? 0 : story.hashCode());
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
      Comment other = (Comment) obj;
      if (description == null) {
         if (other.description != null)
            return false;
      } else if (!description.equals(other.description))
         return false;
      if (author == null) {
         if (other.author != null)
            return false;
      } else if (!author.equals(other.author))
         return false;
      if (story == null) {
         if (other.story != null)
            return false;
      } else if (!story.equals(other.story))
         return false;
      return true;
   }



}
