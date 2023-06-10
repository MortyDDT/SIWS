package it.uniroma3.siw.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private MovieService movieService;

	/******************************************************************************/
	/********************************** ANYONE ************************************/
	/******************************************************************************/

	@PostMapping("/register")
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult userBindingResult, @Valid @ModelAttribute("credentials") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model) {

		// se user e credentials sono validi, memorizzali nel DB
		if (!(userBindingResult.hasErrors() || credentialsBindingResult.hasErrors())) {
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			model.addAttribute("user", user);
			return "registrationSuccessful";
		}
		return "formRegisterUser";
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
		model.addAttribute("movies", movieService.findAll());
		if (AuthUtil.isAdmin())
			return "admin/indexAdmin.html";
		return "index.html";
	}

	@GetMapping("/success")
	public String defaultAfterLogin(Model model) {
		model.addAttribute("movies", movieService.findAll());
		if (AuthUtil.isAdmin())
			return "admin/indexAdmin.html";
		return "index.html";
	}

}