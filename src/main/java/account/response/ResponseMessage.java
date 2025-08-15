package account.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ResponseMessage {

    public Map<String, Object> getResponseBody(int status, String message, String error, String path) {
        Map<String, Object> resp = new LinkedHashMap<>();

        resp.put("timestamp", LocalDateTime.now());
        resp.put("status", status);

        if (error != null) {
            resp.put("error", error);
        }

        if (message != null) {
            resp.put("message", message);
        }

        if (path != null) {
            resp.put("path", path);
        }

        return resp;
    }

    public ResponseEntity<?> getResponseBadRequest(String message, String path) {

        return new ResponseEntity<>(getResponseBody(
                HttpStatus.BAD_REQUEST.value(), message,
                HttpStatus.BAD_REQUEST.getReasonPhrase(), path),
                HttpStatus.BAD_REQUEST);
    }
}
