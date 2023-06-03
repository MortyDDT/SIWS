package it.uniroma3.siw.siwsocial00.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.User;

public interface StoryRepository extends CrudRepository<Story, Long> {

   public List<Story> findByAuthorOrderByIdDesc(User author);

   public List<Story> findByAuthorInOrderByIdDesc(List<User> users);

   public List<Story> findAllByOrderByIdDesc();
   
}
