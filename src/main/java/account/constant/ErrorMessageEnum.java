package account.constant;

public enum ErrorMessageEnum {
    USER_EXIST("User exist!"),
    PASSWORD_BREACHED("The password is in the hacker's database!"),
    PASSWORD_LENGTH("Password length must be 12 chars minimum!"),
    PASSWORDS_SAME("The passwords must be different!"),
    WRONG_DATE("Wrong date!"),
    USER_NOT_FOUND("User not found!"),
    PAYMENT_NOT_FOUND("Payment for update not found!"),
    NEGATIVE_SALARY("Salary must be non negative!"),
    REMOVE_ADMINISTRATOR("Can't remove ADMINISTRATOR role!"),
    ROLE_NOT_FOUND("Role not found!"),
    USER_DOES_NOT_HAVE_A_ROLE("The user does not have a role!"),
    CAN_NOT_LOCK_ADMINISTRATOR("Can't lock the ADMINISTRATOR!"),
    CAN_NOT_COMBINE_ROLES("The user cannot combine administrative and business roles!"),
    ERROR("Error!");

    private final String message;

    ErrorMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
