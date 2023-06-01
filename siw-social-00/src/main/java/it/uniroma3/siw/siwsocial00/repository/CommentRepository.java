package it.uniroma3.siw.siwsocial00.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.model.Story;

public interface CommentRepository extends CrudRepository<Comment, Long> {

   public List<Comment> findByStory(Story story);

}