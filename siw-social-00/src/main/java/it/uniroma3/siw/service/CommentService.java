package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.Story;
import it.uniroma3.siw.repository.CommentRepository;

@Service
public class CommentService {

   @Autowired
   protected CommentRepository commentRepository;

   
   
   
   /******************************************************************************/
   /********************************** CUSTOM ************************************/
   /******************************************************************************/








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
