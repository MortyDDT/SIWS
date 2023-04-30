package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Controller
public class ArtistController {

	@Autowired
	ArtistRepository artistRepository;

	@PostMapping("/showArtists")
	public String newArtist(@ModelAttribute("artist") Artist artist, Model model) {
		if (!artistRepository.existsByNameAndAge(artist.getName(), artist.getAge())) {
			this.artistRepository.save(artist);
			model.addAttribute("artists", artistRepository.findAll());
			return "showArtists.html";
		} else {
			model.addAttribute("messaggioErrore", "Quest'artista e stato gia aggiunto nel database!");
			return "formNewArtist.html";
		}
	}

	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "formNewArtist.html";
	}

}
