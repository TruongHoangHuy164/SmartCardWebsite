// package com.quizletclone.flashcard.service;

// import com.quizletclone.flashcard.model.User;
// import com.quizletclone.flashcard.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;

// @Service
// public class UserService {
//     @Autowired
//     private UserRepository userRepository;

//     public List<User> getAllUsers() {
//         return userRepository.findAll();
//     }

//     public Optional<User> findByUsername(String username) {
//         return userRepository.findByUsername(username);
//     }

//     public Optional<User> findByEmail(String email) {
//         return userRepository.findByEmail(email);
//     }

//     public Optional<User> findById(Integer id) {
//         return userRepository.findById(id);
//     }

//     public User saveUser(User user) {
//         return userRepository.save(user);
//     }

//     public void deleteUser(Integer id) {
//         userRepository.deleteById(id);
//     }
// }

package com.quizletclone.flashcard.service;

import com.quizletclone.flashcard.model.Role;
import com.quizletclone.flashcard.model.User;
import com.quizletclone.flashcard.repository.RoleRepository;
import com.quizletclone.flashcard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public User saveUserWithRole(User user, String roleName) {
        Role role = getRoleByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            role = roleRepository.save(role);
        }
        user.setRole(role);
        return saveUser(user);
    }
}