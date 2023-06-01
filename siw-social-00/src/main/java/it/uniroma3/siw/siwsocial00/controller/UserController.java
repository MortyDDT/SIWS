package it.uniroma3.siw.siwsocial00.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.User;
import it.uniroma3.siw.siwsocial00.service.UserService;
import it.uniroma3.siw.siwsocial00.tool.AuthUtil;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields("name", "surname", "birthDate", "email");

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
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@PostMapping("/modifyUser")
	public String modifyUser(Model model,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "surname", required = false) String surname,
			@RequestParam(value = "birthdate", required = false) LocalDate birthDate,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

		User user = userService.modifyUser(name, surname, birthDate, email, file);
		model.addAttribute("user", user);
		model.addAttribute("messaggioSuccesso", "Il profilo e stato modificato!");
		return AuthUtil.parseLink("manageProfile.html");
	}

	@GetMapping("/manageProfile")
	public String manageUser(Model model) {
		model.addAttribute("user", userService.getCurrentUser());
		return AuthUtil.parseLink("manageProfile.html");
	}

	@GetMapping("/myProfile") /// user to my profile should be received from the template directly from
												/// autheticated user
	public String showMyProfile(Model model) {
		User user = userService.getCurrentUser();
		List<Story> stories = user.getStories();
		model.addAttribute("user", user);
		model.addAttribute("story", new Story());
		model.addAttribute("stories", stories);
		return AuthUtil.parseLink("profile.html");
	}

	@GetMapping("/users")
	public String showUsers(Model model) {

		List<User> users = userService.findAll();
		if(AuthUtil.isUserAuthenticated())
			users.remove(userService.getCurrentUser());
		model.addAttribute("users", users);
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/users/{id}")
	public String showUser(@PathVariable("id") Long id, Model model) {
		User user = userService.getUser(id);
		List<Story> stories = user.getStories();
		model.addAttribute("user", user);
		model.addAttribute("stories", stories);
		return AuthUtil.parseLink("user.html");
	}

	@PostMapping("/searchUser")
	public String searchUser(Model model, @RequestParam(value = "substring") String substring) {
		model.addAttribute("users", userService.searchUser(substring));
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/friends")
	public String showFriends(Model model) {
		model.addAttribute("users", userService.findFriends());
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/friendRequests")
	public String showFriendRequests(Model model) {
		model.addAttribute("users", userService.getCurrentUser().getFriendRequests());
		return AuthUtil.parseLink("friendRequests.html");
	}

	@GetMapping("/removeFriend/{idUser}")
	public String removeFriend(@PathVariable("idUser") Long idUser, Model model) {
		model.addAttribute("users", userService.removeFriend(idUser));
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/sendFriendRequest/{idUser}")
	public String sendFriendRequest(@PathVariable("idUser") Long idUser, Model model) {
		User user = userService.sendFriendRequest(idUser);
		
		model.addAttribute("user", user);
		model.addAttribute("stories", user.getStories());
		return AuthUtil.parseLink("user.html");
	}

	@GetMapping("/acceptFriendRequest/{idUser}")
	public String acceptFriendRequest(@PathVariable("idUser") Long idUser, Model model) {
		model.addAttribute("users", userService.acceptFriendRequest(idUser));
		return AuthUtil.parseLink("friendRequests.html");
	}

	@GetMapping("/declineFriendRequest/{idUser}")
	public String declineFriendRequest(@PathVariable("idUser") Long idUser, Model model) {
		model.addAttribute("users", userService.declineFriendRequest(idUser));
		return AuthUtil.parseLink("friendRequests.html");
	}

}
