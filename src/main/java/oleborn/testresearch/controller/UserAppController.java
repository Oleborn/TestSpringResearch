package oleborn.testresearch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.service.UserAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserAppController {

    private final UserAppService userAppService;

    @PostMapping("/create")
    public ResponseEntity<UserApp> createUserApp(@RequestBody @Valid UserAppDto userAppDto) {
        UserApp userApp = userAppService.create(userAppDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userApp);
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserAppDto> getUserApp(@RequestParam String mail) {
        return ResponseEntity.ok(userAppService.getUser(mail));
    }

}
