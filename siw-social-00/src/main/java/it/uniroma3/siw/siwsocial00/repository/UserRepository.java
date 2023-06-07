package it.uniroma3.siw.siwsocial00.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.siwsocial00.model.User;


public interface UserRepository extends CrudRepository<User, Long> {

   @Query(value = "SELECT u FROM User u WHERE :user MEMBER OF u.friends")
   public List<User> findFriends(@Param("user") User user);

   public boolean existsByEmail(String email);

   public List<User> findByNameContainingIgnoreCase(String title);
   
}