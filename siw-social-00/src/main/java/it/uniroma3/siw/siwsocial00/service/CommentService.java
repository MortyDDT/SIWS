package it.uniroma3.siw.siwsocial00.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.siwsocial00.model.Comment;
import it.uniroma3.siw.siwsocial00.model.Story;
import it.uniroma3.siw.siwsocial00.model.User;
import it.uniroma3.siw.siwsocial00.repository.CommentRepository;
import it.uniroma3.siw.siwsocial00.repository.StoryRepository;
import it.uniroma3.siw.siwsocial00.repository.UserRepository;

@Service
public class CommentService {

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected StoryRepository storyRepository;
    
    @Autowired
    protected UserRepository userRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public Story removeComment(Long id) {
        Comment comment = commentRepository.findById(id).get();
        Story story = comment.getStory();
        
        commentRepository.delete(comment);

        return story;
    }

    @Transactional
    public void addComment(Comment comment, Long storyId, Long userId) {
        Story story = storyRepository.findById(storyId).get();
        User user = userRepository.findById(userId).get();
        comment.setDateAdded(LocalDateTime.now());

        story.getComments().add(comment);
        user.getComments().add(comment);
        
        comment.setAuthor(user);
        comment.setStory(story);
        commentRepository.save(comment);
    }

    /******************************************************************************/
    /********************************** GENERAL ***********************************/
    /******************************************************************************/

    @Transactional
    public Comment findById(Long id) {
        return commentRepository.findById(id).get();
    }

    @Transactional
    public List<Comment> findAll() {
        List<Comment> comments = new ArrayList<>();
        Iterable<Comment> iterable = commentRepository.findAll();
        for (Comment comment : iterable)
            comments.add(comment);

        return comments;
    }

    @Transactional
    public List<Comment> findCommentsByStory(Story story) {
        List<Comment> comments = new ArrayList<>();
        Iterable<Comment> iterable = commentRepository.findByStory(story);
        for (Comment comment : iterable)
            comments.add(comment);

        return comments;
    }

}
