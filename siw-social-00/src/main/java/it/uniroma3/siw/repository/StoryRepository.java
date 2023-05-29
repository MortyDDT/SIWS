package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.model.User;
import java.util.List;


public interface StoryRepository extends CrudRepository<Story, Long>{
   
   public List<Story> findByAuthor(User author);

}
