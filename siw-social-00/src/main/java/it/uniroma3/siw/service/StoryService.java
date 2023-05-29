package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.StoryRepository;

@Service
public class StoryService {
   
   @Autowired
   protected StoryRepository storyRepository;


   /******************************************************************************/
   /********************************** CUSTOM ************************************/
   /******************************************************************************/








   /******************************************************************************/
   /********************************** GENERAL ***********************************/
   /******************************************************************************/

   @Transactional
    public Story findById(Long id) {
        return storyRepository.findById(id).get();
    }

    @Transactional
    public List<Story> findAll() {
        List<Story> stories = new ArrayList<>();
        Iterable<Story> iterable = storyRepository.findAll();
        for (Story story : iterable)
            stories.add(story);

        return stories;
    }

    @Transactional
    public List<Story> findStoriesByUser(User user) {
        List<Story> stories = new ArrayList<>();
        Iterable<Story> iterable = storyRepository.findByAuthor(user);
        for (Story story : iterable)
            stories.add(story);

        return stories;
    }

}
