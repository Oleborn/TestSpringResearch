package oleborn.testresearch.service;

import jakarta.validation.Valid;
import oleborn.testresearch.model.dto.LoginDto;
import oleborn.testresearch.model.dto.RegisterDto;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;

public interface UserAppService {

    UserApp create(UserAppDto userAppDto);

    String registerUser(RegisterDto registerDto);

    String loginUser(@Valid LoginDto loginDto);

    UserAppDto getUser(String mail);

}
