package com.berkay.usermanagement.controller;

import com.berkay.usermanagement.model.Permission;
import com.berkay.usermanagement.model.User;
import com.berkay.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        if (createdUser != null) {
            return ResponseEntity.ok(createdUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/seller-applications")
    public List<User> getAllSellerApplications() {
        return userService.getAllSellerApplications();
    }

    @PostMapping("/seller-applications")
    public ResponseEntity<User> createSellerApplication(@RequestBody User user) {
        User createdUser = userService.createSellerApplication(user);
        if (createdUser != null) {
            return ResponseEntity.ok(createdUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/seller-applications/{id}")
    public ResponseEntity<User> updateSellerApplication(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateSellerApplication(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/seller-applications/{id}")
    public ResponseEntity<Void> deleteSellerApplication(@PathVariable Long id) {
        if (userService.deleteSellerApplication(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/id-applications")
    public List<User> getAllIdApplications() {
        return userService.getAllIdApplications();
    }

    @PostMapping("/id-applications")
    public ResponseEntity<User> createIdApplication(@RequestBody User user) {
        User createdUser = userService.createIdApplication(user);
        if (createdUser != null) {
            return ResponseEntity.ok(createdUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/id-applications/{id}")
    public ResponseEntity<User> updateIdApplication(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateIdApplication(id, user);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/id-applications/{id}")
    public ResponseEntity<Void> deleteIdApplication(@PathVariable Long id) {
        if (userService.deleteIdApplication(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<Set<Permission>> getUserPermissions(@PathVariable Long id) {
        Set<Permission> permissions = userService.getUserPermissions(id);
        if (permissions != null) {
            return ResponseEntity.ok(permissions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> addUserPermission(@PathVariable Long id, @RequestBody Permission permission) {
        userService.addUserPermission(id, permission.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<Void> removeUserPermission(@PathVariable Long id, @PathVariable Long permissionId) {
        userService.removeUserPermission(id, permissionId);
        return ResponseEntity.noContent().build();
    }
}
