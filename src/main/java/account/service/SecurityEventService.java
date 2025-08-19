package account.service;

import account.model.SecurityEvent;
import account.constant.EventEnum;
import account.repository.SecurityEventRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class SecurityEventService {

    private final SecurityEventRepository securityEventRepository;
    private final HttpServletRequest request;

    public SecurityEventService(SecurityEventRepository securityEventRepository, HttpServletRequest request) {
        this.securityEventRepository = securityEventRepository;
        this.request = request;
    }

    public ResponseEntity<?> getAllEvents() {
        return new ResponseEntity<>(securityEventRepository.findAll(),
                HttpStatus.OK);
    }

    public void saveEvent(LocalDateTime timeStamp, EventEnum action, String subject) {
        saveEvent(timeStamp, action, subject, request.getRequestURI());
    }

    public void saveEvent(LocalDateTime timeStamp, EventEnum action, String subject,
                          String object) {

        securityEventRepository.save(
                new SecurityEvent(timeStamp, action, subject, object, request.getRequestURI())
        );
    }
}
