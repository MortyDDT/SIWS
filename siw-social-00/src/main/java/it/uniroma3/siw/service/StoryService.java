package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.StoryRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.tool.FileUploadUtil;

@Service
public class StoryService {

    @Autowired
    protected StoryRepository storyRepository;

    @Autowired
    protected UserRepository userRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public void addStory(Story story, MultipartFile file) throws IOException {

        story.setDateAdded(LocalDate.now());
        story.setLikes(0);

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        story.setImageName(fileName);
        Story savedMovie = storyRepository.save(story);
        String uploadDir = Story.IMAGE_PATH + "/" + savedMovie.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, file);
    }

    @Transactional
    public User likeStory(Long id) {
        Story story = storyRepository.findById(id).get();
        User user = story.getAuthor();

        story.addLike();
        storyRepository.save(story);

        return user;
    }

    @Transactional
    public User removeStory(Long id) {
        Story story = storyRepository.findById(id).get();
        User user = story.getAuthor();

        storyRepository.delete(story);

        return user;
    }

    @Transactional
    public Story modifyStory(Long id, String descrizione, MultipartFile file) throws IOException {

        Story story = storyRepository.findById(id).get();

        if (!(descrizione.isEmpty() || descrizione.isBlank())) // in spring 2 empty fields get parsed as ""
            story.setDescription(descrizione);

        if (file.getSize() > 0) { // in spring 2 isEmpty doesen't work
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            story.setImageName(fileName);
            String uploadDir = Story.IMAGE_PATH + "/" + story.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        return storyRepository.save(story);
    }

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
