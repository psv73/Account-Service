package account.utils;

import account.model.Role;
import account.constant.RoleEnum;
import account.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        createRoles();
    }

    private void createRoles() {

        try {
            for (RoleEnum role : RoleEnum.values()) {
                if (roleRepository.findByRole(role.toString()).isEmpty()) {
                    roleRepository.save(new Role(role.toString()));
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }
}