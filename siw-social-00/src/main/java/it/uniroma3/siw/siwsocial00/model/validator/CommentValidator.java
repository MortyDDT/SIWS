package it.uniroma3.siw.siwsocial00.model.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.siwsocial00.model.Comment;

@Component
public class CommentValidator implements Validator{

   @Override
   public void validate(Object o, Errors errors) {
   }

   @Override
   public boolean supports(Class<?> aClass) {
       return Comment.class.equals(aClass);
   }

}
