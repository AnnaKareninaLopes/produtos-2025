package edu.ifmg.produtos.services;

import edu.ifmg.produtos.dtos.ProductDTO;
import edu.ifmg.produtos.dtos.RoleDTO;
import edu.ifmg.produtos.dtos.UserDTO;
import edu.ifmg.produtos.dtos.UserInsertDTO;
import edu.ifmg.produtos.entities.Product;
import edu.ifmg.produtos.entities.Role;
import edu.ifmg.produtos.entities.User;
import edu.ifmg.produtos.projections.UserDetailsProjection;
import edu.ifmg.produtos.repository.CategoryRepository;
import edu.ifmg.produtos.repository.RoleRepository;
import edu.ifmg.produtos.repository.UserRepository;
import edu.ifmg.produtos.resources.ProductResource;
import edu.ifmg.produtos.services.exceptions.DatabaseException;
import edu.ifmg.produtos.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> users = repository.findAll(pageable);
        return users.map(user -> new UserDTO(user));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> opt = repository.findById(id);

        User user = opt.orElseThrow( () -> new ResourceNotFound("User Not Found") );
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {

        User user = new User();
        copyDtoToEntity(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = repository.save(user);
        return new UserDTO(user);

    }

    @Transactional
    public UserDTO update(UserDTO dto, Long id){

        try{
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);

            return new UserDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFound("User not found" + id);
        }

    }

    @Transactional
    public void delete(Long id){
        if (!repository.existsById(id)){
            throw new ResourceNotFound("User not found" + id);
        }
        try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for(RoleDTO role: dto.getRoles()) {
            Role roleEntity = roleRepository.getReferenceById(role.getId());
            entity.getRoles().add(roleEntity);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result
                = repository.searchUserAndRoleByEmail(username);

        if(result.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }

        User user = new User();
        user.setEmail(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection p : result) {
            user.addRole(new Role(p.getRoleId(), p.getAuthority()));
        }

        return user;
    }

    public UserDTO signup(UserInsertDTO dto) {

        User user = new User();
        copyDtoToEntity(dto, user);

        Role role = roleRepository.findByAuthority("ROLE_OPERATOR");

        user.getRoles().clear();
        user.getRoles().add(role); //inserimos o perfil de operador
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = repository.save(user);
        return new UserDTO(user);

    }
}