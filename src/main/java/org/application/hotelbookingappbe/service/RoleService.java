package org.application.hotelbookingappbe.service;

import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.exception.RoleAlreadyExistsException;
import org.application.hotelbookingappbe.exception.RoleNotFoundException;
import org.application.hotelbookingappbe.model.Role;
import org.application.hotelbookingappbe.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new RoleAlreadyExistsException("Role " + role.getName() + " already exists");
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public void deleteRoleByName(String name) {
        Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + name));
        roleRepository.delete(role);
    }
}
