package pl.sda.springsecurity.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dummy")
public class DummyController {

    @GetMapping()
    public ResponseEntity<String> dummy() {
        return ResponseEntity.ok("Hello");
    }
}
