package it.uniroma3.siw.siwsocial00.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siwsocial00.model.Credentials;
import it.uniroma3.siw.siwsocial00.model.User;
import it.uniroma3.siw.siwsocial00.service.CredentialsService;
import it.uniroma3.siw.siwsocial00.service.StoryService;
import it.uniroma3.siw.siwsocial00.service.UserService;
import it.uniroma3.siw.siwsocial00.tool.AuthUtil;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private StoryService storyService;

	@Autowired
	private UserService userService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("image");

		binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			public void setAsText(String value) {
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate data = LocalDate.parse(value, formatter);
					setValue(data);
				} catch (ParseException | DateTimeException e) {
					setValue(null);
				}
			}
		});
	}

	/******************************************************************************/
	/********************************** ANYONE ************************************/
	/******************************************************************************/

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult userBindingResult, @Valid @ModelAttribute("credentials") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model, @RequestParam("image") MultipartFile file) throws IOException {

		// se user e credentials sono validi, memorizzali nel DB
		if (!(userBindingResult.hasErrors() || credentialsBindingResult.hasErrors())) {
			userService.addImageToUserAndSave(user, file);
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);

			model.addAttribute("user", user);
			return "registrationSuccessful";
		}
		return "registerUser";
	}

	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {
		return "formLoginUser";
	}

	/******************************************************************************/
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@GetMapping("/")
	public String index(Model model) {
		if (AuthUtil.isAdmin())
			return "admin/indexAdmin.html";
		if (AuthUtil.isUserAuthenticated())
			model.addAttribute("stories", storyService.getFriendStories(userService.getCurrentUser()));
		return "index.html";
	}

	@GetMapping("/success")
	public String defaultAfterLogin(Model model) {
		if (AuthUtil.isAdmin())
			return "admin/indexAdmin.html";
		if (AuthUtil.isUserAuthenticated())
			model.addAttribute("stories", storyService.getFriendStories(userService.getCurrentUser()));
		return "index.html";
	}

}