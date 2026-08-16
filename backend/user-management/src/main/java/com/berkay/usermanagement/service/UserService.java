package com.berkay.usermanagement.service;

import com.berkay.usermanagement.model.Permission;
import com.berkay.usermanagement.model.User;
import com.berkay.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            return userRepository.save(user);
        }
        return null;
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<User> getAllSellerApplications() {
        return userRepository.findByIsSellerApplication(true);
    }

    public User createSellerApplication(User user) {
        user.setSellerApplication(true);
        return userRepository.save(user);
    }

    public User updateSellerApplication(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            user.setSellerApplication(true);
            return userRepository.save(user);
        }
        return null;
    }

    public boolean deleteSellerApplication(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<User> getAllIdApplications() {
        return userRepository.findByIsIdApplication(true);
    }

    public User createIdApplication(User user) {
        user.setIdApplication(true);
        return userRepository.save(user);
    }

    public User updateIdApplication(Long id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            user.setIdApplication(true);
            return userRepository.save(user);
        }
        return null;
    }

    public boolean deleteIdApplication(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Set<Permission> getUserPermissions(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getPermissions() : null;
    }

    public void addUserPermission(Long userId, Long permissionId) {
        User user = userRepository.findById(userId).orElse(null);
        Permission permission = new Permission();
        permission.setId(permissionId);
        if (user != null) {
            user.getPermissions().add(permission);
            userRepository.save(user);
        }
    }

    public void removeUserPermission(Long userId, Long permissionId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.getPermissions().removeIf(p -> p.getId().equals(permissionId));
            userRepository.save(user);
        }
    }
}
