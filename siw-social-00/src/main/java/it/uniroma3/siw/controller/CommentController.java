package it.uniroma3.siw.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.validator.CommentValidator;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.StoryService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class CommentController {

   @Autowired
   private CommentService commentService;

   @Autowired
   private StoryService storyService;

   @Autowired
   private CommentValidator commentValidator;

   /******************************************************************************/
   /******************************** FOR ADMINS **********************************/
   /******************************************************************************/

   @GetMapping("/removeUserComment/{id}")
   public String removeUserComment(@PathVariable("id") Long id, Model model) {
      model.addAttribute("story", commentService.removeComment(id));
      return "admin/story.html";
   }

   /******************************************************************************/
   /********************************** SHARED ************************************/
   /******************************************************************************/

   @PostMapping("/addComment/{storyId}/{userId}")
   public String addComment(@Valid @ModelAttribute("comment") Comment comment, BindingResult bindingResult,
         Model model, @PathVariable("storyId") Long storyId, @PathVariable("userId") Long userId) {

      commentValidator.validate(comment, bindingResult);
      if (!bindingResult.hasErrors())
         commentService.addComment(comment, storyId, userId);
      
      model.addAttribute("story", storyService.findById(storyId));
      return AuthUtil.parseLink("story.html");
   }

   @GetMapping("/removeComment/{id}")
   public String removeComment(@PathVariable("id") Long id, Model model) {
      model.addAttribute("story", commentService.removeComment(id));
      return AuthUtil.parseLink("story.html");
   }

}
