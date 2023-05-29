package it.uniroma3.siw.controller;

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

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.UserService;
import it.uniroma3.siw.tool.AuthUtil;

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
	/******************************** FOR ADMINS **********************************/
	/******************************************************************************/

	/******************************************************************************/
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@PostMapping("/modifyUser/{id}")
	public String modifyUser(Model model, @PathVariable("id") Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "surname", required = false) String surname,
			@RequestParam(value = "birthdate", required = false) LocalDate birthDate,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

		User user = userService.modifyUser(id, name, surname, birthDate, email, file);
		model.addAttribute("user", user);
		model.addAttribute("messaggioSuccesso", "Il profilo e stato modificato!");
		return AuthUtil.parseLink("manageUser.html");
	}

	@GetMapping("/manageUser/{id}")
	public String manageUser(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", userService.getUser(id));
		return AuthUtil.parseLink("manageUser.html");
	}

	@GetMapping("/users")
	public String showUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/friends/{id}")
	public String showFriends(@PathVariable("id") Long id, Model model) {
		model.addAttribute("users", userService.findFriends(id));
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/removeFriend/{idUser1}/{idUser2}")
	public String removeFriend(@PathVariable("idUser1") Long idUser1, @PathVariable("idUser2") Long idUser2, Model model) {
		model.addAttribute("users", userService.removeFriend(idUser1, idUser2));
		return AuthUtil.parseLink("users.html");
	}

	@GetMapping("/sendFriendRequest/{idUser1}/{idUser2}")
	public String sendFriendRequest(@PathVariable("idUser1") Long idUser1, @PathVariable("idUser2") Long idUser2, Model model) {
		model.addAttribute("user", userService.sendFriendRequest(idUser1, idUser2));
		return AuthUtil.parseLink("user.html");
	}

	@GetMapping("/acceptFriendRequest/{idUser1}/{idUser2}")
	public String acceptFriendRequest(@PathVariable("idUser1") Long idUser1, @PathVariable("idUser2") Long idUser2, Model model) {
		model.addAttribute("users", userService.acceptFriendRequest(idUser1, idUser2));
		return AuthUtil.parseLink("friendRequests.html");
	}

	@GetMapping("/declineFriendRequest/{idUser1}/{idUser2}")
	public String declineFriendRequest(@PathVariable("idUser1") Long idUser1, @PathVariable("idUser2") Long idUser2, Model model) {
		model.addAttribute("users", userService.declineFriendRequest(idUser1, idUser2));
		return AuthUtil.parseLink("friendRequests.html");
	}

	@GetMapping("/users/{id}")
	public String showUser(@PathVariable("id") Long id, Model model) {
		User user = userService.getUser(id);
		List<Story> stories = user.getStories();
		model.addAttribute("user", user);
		model.addAttribute("stories", stories);
		return AuthUtil.parseLink("user.html");
	}

	@GetMapping("/myProfile/{id}") /// user to my profile should be received from the template directly from
												/// autheticated user
	public String showMyProfile(@PathVariable("id") Long id, Model model) {
		User user = userService.getUser(id);
		List<Story> stories = user.getStories();
		model.addAttribute("user", user);
		model.addAttribute("stories", stories);
		return AuthUtil.parseLink("profile.html");
	}

}
