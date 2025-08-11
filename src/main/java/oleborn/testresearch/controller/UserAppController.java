package oleborn.testresearch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.service.UserAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
