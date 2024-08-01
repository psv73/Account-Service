package account.request;

import account.model.enums.AccessEnum;

public class UserAccessRequest {

    private String user;
    private AccessEnum operation;

    public UserAccessRequest() {
    }

    public UserAccessRequest(String user, AccessEnum operation) {
        this.user = user;
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public AccessEnum getOperation() {
        return operation;
    }

    public void setOperation(AccessEnum operation) {
        this.operation = operation;
    }
}
