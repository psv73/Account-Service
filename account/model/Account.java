package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@NamedQueries({
        @NamedQuery(name = "Account.findAll", query = "select a from Account a")
})
public class Account {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    @JsonProperty("name")
    private String name;

    @NotBlank
    private String lastName;

    @NotBlank
    @Pattern(regexp = ".+@acme\\.com")
    private String email;

    @NotBlank(message = "Password length must be 12 chars minimum!")
    @Size(min = 12)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new LinkedHashSet<>();

    @JsonIgnore
    @Column(columnDefinition = "boolean default true")
    private Boolean accountNotExpired = true;

    @JsonIgnore
    @Column(columnDefinition = "boolean default true")
    private Boolean accountNotLocked = true;

    @JsonIgnore
    @Column(columnDefinition = "boolean default true")
    private Boolean credentialsNonExpired = true;

    @JsonIgnore
    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = true;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer failedAttempt = 0;

    @JsonIgnore
    private Date lockTime;

    public Account() {
    }

    public Account(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email.toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean isAccountNotExpired() {
        return accountNotExpired;
    }

    public void setAccountNotExpired(Boolean accountNotExpired) {
        this.accountNotExpired = accountNotExpired;
    }

    public Boolean isAccountNotLocked() {
        return accountNotLocked;
    }

    public void setAccountNotLocked(Boolean accountNotLocked) {
        this.accountNotLocked = accountNotLocked;
    }

    public Boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public int getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(int failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }
}
