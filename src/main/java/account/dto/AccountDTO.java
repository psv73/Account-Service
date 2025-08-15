package account.dto;

import account.model.Role;

import java.util.Set;
import java.util.TreeSet;

public class AccountDTO implements Comparable<AccountDTO> {

    private Integer id;

    private String name;

    private String lastName;

    private String email;

    private final Set<String> roles = new TreeSet<>();

    public AccountDTO() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        for (Role role : roles) {
            this.roles.add(role.getRole());
        }
    }

    @Override
    public int compareTo(AccountDTO accountDTO) {
        return this.getId() - accountDTO.getId();
    }
}
