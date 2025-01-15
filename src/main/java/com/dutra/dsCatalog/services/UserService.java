package com.dutra.dsCatalog.services;

import com.dutra.dsCatalog.dtos.RoleDto;
import com.dutra.dsCatalog.dtos.UserDto;
import com.dutra.dsCatalog.dtos.UserInsertDto;
import com.dutra.dsCatalog.dtos.UserUpdateDto;
import com.dutra.dsCatalog.entities.Role;
import com.dutra.dsCatalog.entities.User;
import com.dutra.dsCatalog.repositories.RoleRepository;
import com.dutra.dsCatalog.repositories.UserRepository;
import com.dutra.dsCatalog.repositories.projections.UserDetailsProjection;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository,
                       RoleRepository roleRepository,
                       @Lazy PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> projections = repository.searchUserAndRolesByEmail(username);

        if (projections.size() == 0) {
            throw new UsernameNotFoundException("Username not found.");
        }

        User user = new User();
        user.setPassword(projections.get(0).getPassword());
        user.setEmail(projections.get(0).getUsername());

        for (UserDetailsProjection projectionEntity : projections) {
            user.addRole(new Role(projectionEntity.getRoleId(), projectionEntity.getAuthority()));
        }
        return user;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(UserDto::new);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        return new UserDto(repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category not found!")
        ));
    }

    @Transactional
    public UserDto InsertUser(UserInsertDto userInsertDto) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(userInsertDto.getPassword()));

        copyToEntity(user, userInsertDto);

        return new UserDto(repository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        try {
            User user = repository.getReferenceById(id);

            copyToEntity(user, userUpdateDto);

            return new UserDto(repository.save(user));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found!");
        }
    }

    private void copyToEntity(User user, UserDto userDto) {
        user.setFirstName(userDto.getFirstName());
        user.setLastname(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        user.getRoles().clear();
        for (RoleDto roleDto : userDto.getRoles()) {
            Role role = roleRepository.getReferenceById(roleDto.getId());
            user.getRoles().add(role);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found!");
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Referential integrity violation.");
        }
    }
}
