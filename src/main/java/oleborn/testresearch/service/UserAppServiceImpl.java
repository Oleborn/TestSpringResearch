package oleborn.testresearch.service;

import lombok.RequiredArgsConstructor;
import oleborn.testresearch.exception.AccessDeniedException;
import oleborn.testresearch.feignclient.UserFeignClient;
import oleborn.testresearch.mapper.UserAppMapper;
import oleborn.testresearch.model.dto.LoginDto;
import oleborn.testresearch.model.dto.RegisterDto;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.repository.AuthUserAppRepository;
import oleborn.testresearch.repository.UserAppRepository;
import oleborn.testresearch.security.JwtUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

    private final UserAppRepository userAppRepository;
    private final UserAppMapper userAppMapper;
    private final UserFeignClient userFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserAppRepository authUserAppRepository;
    private final JwtUtils jwtUtils;

    @Override
    public UserApp create(UserAppDto userAppDto) {

        Optional <UserApp> userApp = userAppRepository.findByMail((userAppDto.mail()));

        if (userApp.isPresent()) {
            throw new RuntimeException("User app already exists");
        }

        Boolean access = userFeignClient.getAccess(userAppDto.mail());
        if (!access) {
            throw new AccessDeniedException();
        }

        UserApp inputUserApp = userAppMapper.fromUserAppDto(userAppDto);

        UserApp savedUserApp = userAppRepository.save(inputUserApp);

        return savedUserApp;
    }

    @Override
    public String registerUser(RegisterDto registerDto) {

        if (userAppRepository.existsByMail(registerDto.mail())) {
            throw new RuntimeException("User app already exists");
        }

        UserApp userApp = create(new UserAppDto(registerDto.name(), registerDto.mail()));

        AuthUserApp authUserApp = AuthUserApp.builder()
                .mail(registerDto.mail())
                .password(passwordEncoder.encode(registerDto.password()))
                .roles(List.of("USER"))
                .userApp(userApp)
                .build();

        AuthUserApp saved = authUserAppRepository.save(authUserApp);

        String token = jwtUtils.generateToken(saved.getMail(), saved.getRoles());

        return token;
    }

    @Override
    public String loginUser(LoginDto loginDto) {

        Optional<AuthUserApp> authUserApp = authUserAppRepository.findByMailAndPassword(loginDto.mail(), passwordEncoder.encode(loginDto.password()));
        if (authUserApp.isEmpty()) {
            throw new RuntimeException("User app does not exist");
        }

        String token = jwtUtils.generateToken(authUserApp.get().getMail(), authUserApp.get().getRoles());

        return token;
    }

    @Override
    @Cacheable(value = "users", key = "#mail")
    public UserAppDto getUser(String mail) {

        Optional<UserApp> byMail = userAppRepository.findByMail(mail);

        if (byMail.isEmpty()) {
            throw new RuntimeException("User app does not exist");
        }

        return userAppMapper.toUserAppDto(byMail.get());

    }
}
