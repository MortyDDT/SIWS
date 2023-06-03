package it.uniroma3.siw.siwsocial00.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.User;
import it.uniroma3.siw.siwsocial00.model.validator.StoryValidator;
import it.uniroma3.siw.siwsocial00.service.StoryService;
import it.uniroma3.siw.siwsocial00.service.UserService;
import it.uniroma3.siw.siwsocial00.tool.AuthUtil;

@Controller
public class StoryController {

   @Autowired
   private StoryService storyService;

   @Autowired
   private UserService userService;

   @Autowired
   private StoryValidator storyValidator;

   @InitBinder
   public void initBinder(WebDataBinder binder) {
      binder.setAllowedFields("description");
   }

   /******************************************************************************/
   /******************************** FOR ADMINS **********************************/
   /******************************************************************************/

   @GetMapping("/removeUserStory/{id}")
   public String removeUserStory(@PathVariable("id") Long id, Model model) {
      User user = storyService.removeStory(id);

      model.addAttribute("user", user);
      model.addAttribute("stories", user.getStories());
      return "admin/user.html";
   }

   /******************************************************************************/
   /********************************** SHARED ************************************/
   /******************************************************************************/

   @PostMapping("/addStory")
   public String addStory(@Valid @ModelAttribute("story") Story story, BindingResult bindingResult,
         Model model, @RequestParam("image") MultipartFile file) throws IOException {

      User user = userService.getCurrentUser();

      storyValidator.validate(story, bindingResult);
      if (!bindingResult.hasErrors())
         storyService.addStory(story, user, file);

      List<Story> stories = user.getStories();
      model.addAttribute("story", new Story());
      model.addAttribute("stories", stories);
      model.addAttribute("user", user);

      return AuthUtil.parseLink("loadingPage.html");
   }

   @GetMapping("/formNewStory")
   public String formNewStory(Model model) {
      model.addAttribute("story", new Story());
      return AuthUtil.parseLink("formNewStory.html");
   }

   @GetMapping("/likeStory/{id}")
   public String likeStory(@PathVariable("id") Long id, Model model) {
      User currentUser = userService.getCurrentUser();
      User author = storyService.likeStory(id, currentUser);

      model.addAttribute("user", author);
      model.addAttribute("stories", storyService.findStoriesByUser(author));
      if(currentUser.getFriends().contains(author))
			model.addAttribute("alreadyFriends", 1);
      return AuthUtil.parseLink("user.html");
   }

   @GetMapping("/likeStoryIndex/{id}")
   public String likeStoryIndex(@PathVariable("id") Long id, Model model) {
      User currentUser = userService.getCurrentUser();
      storyService.likeStory(id, currentUser);

      model.addAttribute("stories", storyService.getFriendStories(currentUser));
      if(AuthUtil.isAdmin())
         return "admin/indexAdmin.html";
      return "index.html";
   }

   @GetMapping("/removeStory/{id}")
   public String removeStory(@PathVariable("id") Long id, Model model) {
      User user = storyService.removeStory(id);

      model.addAttribute("user", user);
      model.addAttribute("story", new Story());
      model.addAttribute("stories", user.getStories());
      return AuthUtil.parseLink("profile.html");
   }

   @GetMapping("/manageStory/{id}")
   public String manageStory(@PathVariable("id") Long id, Model model) {
      model.addAttribute("story", storyService.findById(id));
      return AuthUtil.parseLink("manageStory.html");
   }

   @PostMapping("/modifyStory/{id}")
   public String modifyStory(Model model, @PathVariable("id") Long id,
         @RequestParam(value = "descrizione", required = false) String descrizione,
         @RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

      Story story = storyService.modifyStory(id, descrizione, file);

      model.addAttribute("story", story);
      model.addAttribute("messaggioSuccesso", "La storia e stata aggiornata!");
      return AuthUtil.parseLink("manageStory.html");
   }

   @GetMapping("/stories/{id}")
   public String getMovie(@PathVariable("id") Long id, Model model) {
      Story story = storyService.findById(id);

      model.addAttribute("story", story);
      model.addAttribute("authenticatedUser", userService.getCurrentUser());
      model.addAttribute("comment", new Comment());
      model.addAttribute("comments", story.getComments());
      return AuthUtil.parseLink("story.html");
   }

}
