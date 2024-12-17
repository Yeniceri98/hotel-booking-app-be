package org.application.hotelbookingappbe.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.model.Role;
import org.application.hotelbookingappbe.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Role Controller", description = "Role API")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @Tag(name = "Get All Roles")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @Tag(name = "Create Role")
    @PostMapping("/create-role")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @Tag(name = "Add Role to User")
    @PostMapping("/add-role-to-user/{userId}/{roleId}")
    public ResponseEntity<String> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.addRoleToUser(userId, roleId);
        return new ResponseEntity<>("User added to role successfully", HttpStatus.OK);
    }

    @Tag(name = "Remove Role from User")
    @DeleteMapping("/remove-role-from-user/{userId}/{roleId}")
    public ResponseEntity<String> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
        return new ResponseEntity<>("Role removed from user successfully", HttpStatus.OK);
    }

    @Tag(name = "Delete Role")
    @DeleteMapping("/delete-role/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return new ResponseEntity<>("Role deleted successfully", HttpStatus.OK);
    }
}
