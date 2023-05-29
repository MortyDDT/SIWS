package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.User;


public interface UserRepository extends CrudRepository<User, Long> {

   @Query(value = "SELECT u FROM User u WHERE :user MEMBER OF u.friends")
   public List<User> findFriends(@Param("user") User user);

   public List<User> findByNameContainingIgnoreCase(String title);

   
}