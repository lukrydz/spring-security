package pl.sda.springsecurity.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dummy")
public class DummyController {

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> dummy() {
        return ResponseEntity.ok("Hello");
    }
}
