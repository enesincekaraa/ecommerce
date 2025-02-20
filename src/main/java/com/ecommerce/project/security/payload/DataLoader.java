package com.ecommerce.project.security.payload;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {


    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        addRoleIfNotExists(AppRole.ROLE_ADMIN);
        addRoleIfNotExists(AppRole.ROLE_USER);
        addRoleIfNotExists(AppRole.ROLE_SELLER);
    }

    private void addRoleIfNotExists(AppRole role) {
        if (roleRepository.findByRoleName(role).isEmpty()) {
            roleRepository.save(new Role(role));
            System.out.println(role + " added!");
        }
    }
}
