package org.application.hotelbookingappbe.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.application.hotelbookingappbe.exception.RoleAlreadyExistsException;
import org.application.hotelbookingappbe.exception.RoleNotFoundException;
import org.application.hotelbookingappbe.model.Role;
import org.application.hotelbookingappbe.model.User;
import org.application.hotelbookingappbe.repository.RoleRepository;
import org.application.hotelbookingappbe.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(Role role) {
        String roleName = "ROLE_" + role.getName().toUpperCase();

        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException(roleName + " is already exists");
        }

        role.setName(roleName);
        return roleRepository.save(role);
    }

    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getRoles().contains(role)) {
            role.addRoleToUser(user);
            roleRepository.save(role);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(Long userId, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        role.removeRoleFromUser(user);
        roleRepository.save(role);
        userRepository.save(user);
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("Role not found"));
        roleRepository.delete(role);
    }
}
