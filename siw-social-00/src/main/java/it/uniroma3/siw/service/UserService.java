package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.tool.AuthUtil;
import it.uniroma3.siw.tool.FileUploadUtil;

/**
 * The UserService handles logic for Users.
 */
@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected CredentialsRepository credentialsRepository;

    /******************************************************************************/
    /********************************** CUSTOM ************************************/
    /******************************************************************************/

    @Transactional
    public void addImageToUserAndSave(User user, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        user.setImageName(fileName);
        User savedUser = userRepository.save(user);
        String uploadDir = User.IMAGE_PATH + "/" + savedUser.getId();

        FileUploadUtil.saveFile(uploadDir, fileName, file);
    }

    @Transactional
    public User modifyUser(String name, String surname, LocalDate birthDate, String email,
            MultipartFile file) throws IOException {

        User user = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();

        if (!(name.isEmpty() || name.isBlank())) // in spring 2 empty fields get parsed as ""
            user.setName(name);

        if (!(surname.isEmpty() || surname.isBlank()))
            user.setSurname(surname);

        if (birthDate != null)
            user.setBirthDate(birthDate);

        if (!(email.isEmpty() || email.isBlank()))
            user.setEmail(email);

        if (file.getSize() > 0) { // in spring 2 isEmpty doesen't work
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            user.setImageName(fileName);
            String uploadDir = User.IMAGE_PATH + "/" + user.getId();

            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }

        return userRepository.save(user);
    }

    @Transactional
    public List<User> removeFriend(Long idUser) {
        User user1 = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();
        User user2 = userRepository.findById(idUser).get();

        user1.getFriends().remove(user2);
        user1.getFriendOf().remove(user2);

        user2.getFriends().remove(user1);
        user2.getFriendOf().remove(user1);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1.getFriends();
    }

    @Transactional
    public User sendFriendRequest(Long idUser) {
        User user1 = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();
        User user2 = userRepository.findById(idUser).get();

        user1.getRequestedFriendships().add(user2);
        user2.getFriendRequests().add(user1);

        userRepository.save(user1);
        userRepository.save(user2);

        return user2;
    }

    @Transactional
    public List<User> acceptFriendRequest(Long idUser) {
        User user1 = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();
        User user2 = userRepository.findById(idUser).get();

        user1.getFriendRequests().remove(user2);
        user2.getRequestedFriendships().remove(user1);

        user1.getFriends().add(user2);
        user1.getFriendOf().add(user2);
        user2.getFriends().add(user1);
        user2.getFriendOf().add(user1);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1.getFriendRequests();
    }

    @Transactional
    public List<User> declineFriendRequest(Long idUser) {
        User user1 = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();
        User user2 = userRepository.findById(idUser).get();

        user1.getFriendRequests().remove(user2);
        user2.getRequestedFriendships().remove(user1);

        userRepository.save(user1);
        userRepository.save(user2);

        return user1.getFriendRequests();
    }

    /******************************************************************************/
    /********************************** GENERAL ***********************************/
    /******************************************************************************/

    /**
     * This method retrieves a User from the DB based on its ID.
     * 
     * @param id the id of the User to retrieve from the DB
     * @return the retrieved User, or null if no User with the passed ID could be
     *         found in the DB
     */
    @Transactional
    public User getUser(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    /**
     * This method saves a User in the DB.
     * 
     * @param user the User to save into the DB
     * @return the saved User
     * @throws DataIntegrityViolationException if a User with the same username
     *                                         as the passed User already exists in
     *                                         the DB
     */
    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    /**
     * This method retrieves all Users from the DB.
     * 
     * @return a List with all the retrieved Users
     */
    @Transactional
    public List<User> findAll() {
        List<User> result = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findAll();
        for (User user : iterable)
            result.add(user);
        return result;
    }

    @Transactional
    public List<User> findFriends() {
        User user = credentialsRepository.findByUsername(AuthUtil.getCurrentUsername()).get().getUser();

        List<User> users = new ArrayList<>();
        Iterable<User> iterable = this.userRepository.findFriends(user);
        for (User u : iterable)
            users.add(u);
        return users;
    }

    @Transactional
    public List<User> searchUser(String name) {
        return this.userRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public User getCurrentUser() {
        String currentUsername = AuthUtil.getCurrentUsername();
        Credentials credentials = credentialsRepository.findByUsername(currentUsername).get();
        return credentials.getUser();
    }

}
