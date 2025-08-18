package oleborn.testresearch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleborn.testresearch.model.dto.LoginDto;
import oleborn.testresearch.model.dto.RegisterDto;
import oleborn.testresearch.service.UserAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthUserController {

    private final UserAppService userAppService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid RegisterDto registerDto) {
        return ResponseEntity.status(201).body(userAppService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginDto loginDto) {
        return ResponseEntity.status(200).body(userAppService.loginUser(loginDto));
    }

}
