package it.uniroma3.siw.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.service.StoryService;

@Component
public class StoryValidator implements Validator{
   
   @Autowired
   private StoryService storyService;

   @Override
   public void validate(Object o, Errors errors) {
       Story story = (Story) o;
   }

   @Override
   public boolean supports(Class<?> aClass) {
       return Story.class.equals(aClass);
   }

}
