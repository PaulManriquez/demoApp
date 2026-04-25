package com.demoApp.demoApp.service;

import com.demoApp.demoApp.dto.UserForm;
import com.demoApp.demoApp.entity.Role;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.RoleRepository;
import com.demoApp.demoApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserAdminService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAllWithRoles();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Message createUser(UserForm form) {
        if (form.getId() != null) {
            return new Message("El usuario ya existe", false);
        }
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            return new Message("La contrasena es obligatoria", false);
        }

        User user = new User();
        applyForm(user, form, true);
        userRepository.save(user);
        return new Message("Usuario creado con exito", true);
    }

    public Message updateUser(UserForm form) {
        if (form.getId() == null) {
            return new Message("El usuario no existe", false);
        }

        User user = userRepository.findById(form.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        boolean setPassword = form.getPassword() != null && !form.getPassword().isBlank();
        applyForm(user, form, setPassword);
        userRepository.save(user);
        return new Message("Usuario actualizado con exito", true);
    }

    private void applyForm(User user, UserForm form, boolean setPassword) {
        user.setName(form.getName());
        user.setLastName(form.getLastName());
        user.setUsername(form.getUsername());
        user.setPhone(form.getPhone());
        user.setEmail(form.getEmail());
        user.setStatus(Boolean.TRUE.equals(form.getStatus()));

        if (setPassword) {
            user.setPassword(passwordEncoder.encode(form.getPassword()));
        }

        Set<Role> roles = new HashSet<>();
        if (form.getRoleIds() != null) {
            for (Integer roleId : form.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new IllegalArgumentException("Rol no existe"));
                roles.add(role);
            }
        }
        user.setRoles(roles);
    }
}

