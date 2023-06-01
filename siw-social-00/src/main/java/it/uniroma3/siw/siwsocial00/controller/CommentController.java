package it.uniroma3.siw.siwsocial00.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.validator.CommentValidator;
import it.uniroma3.siw.siwsocial00.service.CommentService;
import it.uniroma3.siw.siwsocial00.service.StoryService;
import it.uniroma3.siw.siwsocial00.service.UserService;
import it.uniroma3.siw.siwsocial00.tool.AuthUtil;

@Controller
public class CommentController {

   @Autowired
   private CommentService commentService;

   @Autowired
   private StoryService storyService;

   @Autowired
   private UserService userService;

   @Autowired
   private CommentValidator commentValidator;

   @InitBinder
   public void initBinder(WebDataBinder binder) {
      binder.setAllowedFields("description");
   }

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
      
      Story story = storyService.findById(storyId);

      model.addAttribute("comment", new Comment());
      model.addAttribute("comments", story.getComments());
      model.addAttribute("story", story);
      model.addAttribute("authenticatedUser", userService.getCurrentUser());

      return AuthUtil.parseLink("story.html");
   }

   @GetMapping("/removeComment/{id}")
   public String removeComment(@PathVariable("id") Long id, Model model) {

      Story story = commentService.removeComment(id);

      model.addAttribute("comment", new Comment());
      model.addAttribute("comments", story.getComments());
      model.addAttribute("story", story);
      model.addAttribute("authenticatedUser", userService.getCurrentUser());

      return AuthUtil.parseLink("story.html");
   }

}
