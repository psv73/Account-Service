package account.controller;

import account.constant.AppPath;
import account.request.ChangeRoleRequest;
import account.request.UserAccessRequest;
import account.service.AdministrationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdministrationController {

    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping(AppPath.USER)
    public ResponseEntity<?> getUsers() {
        return administrationService.getAllUsers();
    }

    @DeleteMapping(AppPath.USER + "/{email}")
    public ResponseEntity<?> deleteUser(Authentication auth, @PathVariable String email) {
        return administrationService.deleteUser(email, auth.getName());
    }

    @PutMapping(AppPath.USER_ROLE)
    public ResponseEntity<?> roleOperation(Authentication auth,
                                           @RequestBody @NotNull ChangeRoleRequest changeRoleRequest) {
        return administrationService.changeUserRole(auth.getName(), changeRoleRequest);
    }

    @PutMapping(AppPath.USER_ACCESS)
    public ResponseEntity<?> access(Authentication auth,
                                    @RequestBody @NotNull UserAccessRequest userAccessRequest) {
        return administrationService.changeUserAccess(userAccessRequest, auth);
    }
}
