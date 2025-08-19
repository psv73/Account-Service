package account.constant;

public enum RoleEnum {
    ROLE_ADMINISTRATOR("ADMINISTRATOR"),
    ROLE_ACCOUNTANT("ACCOUNTANT"),
    ROLE_USER("USER"),
    ROLE_AUDITOR("AUDITOR");

    private final String role;

    RoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
