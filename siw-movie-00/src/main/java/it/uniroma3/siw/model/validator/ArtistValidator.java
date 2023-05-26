package it.uniroma3.siw.model.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Component
public class ArtistValidator implements Validator {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public void validate(Object o, Errors errors) {
        Artist artist = (Artist) o;
        String name = artist.getName();
        String surname = artist.getSurname();

        if ((name != null && surname != null) && artistRepository.existsByNameAndSurname(name, surname))
            errors.reject("artist.duplicate");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Artist.class.equals(aClass);
    }

}
