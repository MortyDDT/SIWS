package it.uniroma3.siw.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class Comment {

   @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

   private String descrizione; 

   @ManyToOne
	private List<User> author;

   @ManyToOne
	private List<Story> story;



   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getDescrizione() {
      return descrizione;
   }

   public void setDescrizione(String descrizione) {
      this.descrizione = descrizione;
   }

   public List<User> getAuthor() {
      return author;
   }

   public void setAuthor(List<User> author) {
      this.author = author;
   }

   public List<Story> getStory() {
      return story;
   }

   public void setStory(List<Story> story) {
      this.story = story;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((descrizione == null) ? 0 : descrizione.hashCode());
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
      if (descrizione == null) {
         if (other.descrizione != null)
            return false;
      } else if (!descrizione.equals(other.descrizione))
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
