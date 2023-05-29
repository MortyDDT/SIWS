package it.uniroma3.siw.controller;

import java.io.IOException;

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

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.model.validator.StoryValidator;
import it.uniroma3.siw.service.StoryService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class StoryController {

   @Autowired
   private StoryService storyService;

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

      storyValidator.validate(story, bindingResult); // verifica errori nei campi inseriti
      if (!bindingResult.hasErrors()) {
         storyService.addStory(story, file);
         model.addAttribute("story", new Story());
         model.addAttribute("messaggioSuccesso", "La storia e stato aggiunta!");
      }
      return AuthUtil.parseLink("formNewStory.html");
   }

   @GetMapping("/formNewStory")
   public String formNewStory(Model model) {
      model.addAttribute("story", new Story());
      return AuthUtil.parseLink("formNewStory.html");
   }

   @GetMapping("/likeStory/{id}")
   public String likeStory(@PathVariable("id") Long id, Model model) {
      User user = storyService.likeStory(id);

      model.addAttribute("user", user);
      model.addAttribute("stories", user.getStories());
      return AuthUtil.parseLink("profile.html");
   }

   @GetMapping("/removeStory/{id}")
   public String removeStory(@PathVariable("id") Long id, Model model) {
      User user = storyService.removeStory(id);

      model.addAttribute("user", user);
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
      model.addAttribute("comments", story.getComments());
      return AuthUtil.parseLink("story.html");
   }

}
