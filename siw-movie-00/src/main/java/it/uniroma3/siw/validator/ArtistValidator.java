package it.uniroma3.siw.validator;

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
        Integer age = artist.getAge();

        if ((name != null && age != null) && artistRepository.existsByNameAndAge(name, age))
            errors.reject("artist.duplicate");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Artist.class.equals(aClass);
    }

}
