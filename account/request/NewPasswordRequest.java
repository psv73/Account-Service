package account.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewPasswordRequest {

    @NotBlank
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @JsonProperty("new_password")
    private String password;

    public NewPasswordRequest() {
    }

    public NewPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
