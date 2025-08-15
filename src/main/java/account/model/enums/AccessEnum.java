package account.model.enums;

public enum AccessEnum {
    LOCK("locked", false),
    UNLOCK("unlocked", true);

    private final String status;
    private final Boolean value;

    AccessEnum(String status, Boolean value) {
        this.status = status;
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getValue() {
        return value;
    }
}
