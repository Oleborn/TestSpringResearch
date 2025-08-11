package oleborn.testresearch.service;

import lombok.RequiredArgsConstructor;
import oleborn.testresearch.exception.AccessDeniedException;
import oleborn.testresearch.feignclient.UserFeignClient;
import oleborn.testresearch.mapper.UserAppMapper;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.repository.UserAppRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppServiceImpl implements UserAppService {

    private final UserAppRepository userAppRepository;
    private final UserAppMapper userAppMapper;
    private final UserFeignClient userFeignClient;

    @Override
    public UserApp create(UserAppDto userAppDto) {

        Optional <UserApp> userApp = userAppRepository.findById(userAppDto.id());

        if (userApp.isPresent()) {
            throw new RuntimeException("User app already exists");
        }

        Boolean access = userFeignClient.getAccess(userAppDto.id());
        if (!access) {
            throw new AccessDeniedException();
        }

        UserApp inputUserApp = userAppMapper.fromUserAppDto(userAppDto);

        UserApp savedUserApp = userAppRepository.save(inputUserApp);

        return savedUserApp;
    }
}
