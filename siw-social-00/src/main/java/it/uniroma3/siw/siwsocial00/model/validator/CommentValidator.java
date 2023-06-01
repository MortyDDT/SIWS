package it.uniroma3.siw.siwsocial00.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.service.CommentService;

@Component
public class CommentValidator implements Validator{
   
   @Autowired
   private CommentService commentService;

   @Override
   public void validate(Object o, Errors errors) {
       Comment comment = (Comment) o;
   }

   @Override
   public boolean supports(Class<?> aClass) {
       return Comment.class.equals(aClass);
   }

}
