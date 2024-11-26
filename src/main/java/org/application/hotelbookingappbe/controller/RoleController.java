package org.application.hotelbookingappbe.controller;

import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.model.Role;
import org.application.hotelbookingappbe.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Role> getRoleByName(@PathVariable String name) {
        return new ResponseEntity<>(roleService.getRoleByName(name), HttpStatus.OK);
    }

    @PostMapping("/{userId}/{roleId}")
    public ResponseEntity<String> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.addRoleToUser(userId, roleId);
        return new ResponseEntity<>("User added to role successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/{roleId}")
    public ResponseEntity<String> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
        return new ResponseEntity<>("Role removed from user successfully", HttpStatus.OK);
    }

    @DeleteMapping("/users/{roleId}")
    public ResponseEntity<String> removeAllRolesFromUser(@PathVariable Long roleId) {
        roleService.removeAllRolesFromUser(roleId);
        return new ResponseEntity<>("All roles removed from user successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }
}
