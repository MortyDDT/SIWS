package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Comment;
import java.util.List;
import it.uniroma3.siw.model.Story;

public interface CommentRepository extends CrudRepository<Comment, Long> {

   public List<Comment> findByStory(Story story);

}