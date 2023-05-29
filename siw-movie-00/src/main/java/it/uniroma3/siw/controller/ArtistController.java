package it.uniroma3.siw.controller;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.validator.ArtistValidator;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.tool.AuthUtil;

@Controller
public class ArtistController {

	@Autowired
	ArtistService artistService;
	@Autowired
	ArtistValidator artistValidator;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setAllowedFields("name", "surname", "birthDate", "deathDate");

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

	@PostMapping("/addArtist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model,
			@RequestParam("image") MultipartFile file) throws IOException {
		artistValidator.validate(artist, bindingResult);

		if (!bindingResult.hasErrors()) {
			List<Artist> artists = artistService.addArtist(artist, file);
			model.addAttribute("artists", artists);
			model.addAttribute("messaggioSuccesso", "L'artista e stato aggiunto!");
		}

		List<ObjectError> errors = bindingResult.getAllErrors();
		for (ObjectError oe : errors)
			System.out.println(oe);
		return "admin/formNewArtist.html";
	}

	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}

	@PostMapping("/modifyArtist/{artistId}")
	public String modifyArtist(Model model, @PathVariable("artistId") Long artistId,
			@RequestParam(value = "nm", required = false) String name,
			@RequestParam(value = "sn", required = false) String surname,
			@RequestParam(value = "bd", required = false) LocalDate birthDate,
			@RequestParam(value = "dd", required = false) LocalDate deathDate,
			@RequestParam(value = "image", required = false) MultipartFile file) throws IOException {

		Artist artist = artistService.modifyArtist(artistId, name, surname, birthDate, deathDate, file);
		model.addAttribute("artist", artist);
		model.addAttribute("messaggioSuccesso", "L'artista e stato modificato!");
		return "admin/manageArtist.html";
	}

	@GetMapping("/manageArtist/{id}")
	public String manageArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", artistService.findById(id));
		return "admin/manageArtist.html";
	}

	@GetMapping("/removeArtist/{id}")
	public String removeArtist(@PathVariable("id") Long id, Model model) {
		List<Artist> artists = artistService.removeArtist(id);

		model.addAttribute("artists", artists);
		return "admin/artists.html";
	}

	/******************************************************************************/
	/********************************** SHARED ************************************/
	/******************************************************************************/

	@GetMapping("/artists")
	public String showArtists(Model model) {
		model.addAttribute("artists", artistService.findAll());
		return AuthUtil.parseLink("artists.html");
	}

	@GetMapping("/artists/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", artistService.findById(id));
		return AuthUtil.parseLink("artist.html");
	}

}
