package it.uniroma3.siw.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.validator.ArtistValidator;

@Controller
public class ArtistController {

	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ArtistValidator artistValidator;

	@PostMapping("/addArtist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {		
		artistValidator.validate(artist, bindingResult);

		if (!bindingResult.hasErrors()) {
			artistRepository.save(artist);
			
			model.addAttribute("artists", artistRepository.findAll());
			return "showArtists.html";
		} else {
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError oe : errors)
				System.out.println(oe);
			
			return "formNewArtist.html";
		}
	}

	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "formNewArtist.html";
	}

}
