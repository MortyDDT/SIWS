package it.uniroma3.siw.siwsocial00.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.User;

public interface CommentRepository extends CrudRepository<Comment, Long> {

   public List<Comment> findByStory(Story story);

   public List<Comment> findByStoryAndAuthor(Story story, User author);
   
   @Query(value = "SELECT c FROM Comment c WHERE c.story=:story AND c.author!=:user")
   public List<Comment> findByStoryWhereUserNotMemberOf(@Param("story") Story story, @Param("user") User user);
}