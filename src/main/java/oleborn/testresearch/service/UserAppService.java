package oleborn.testresearch.service;

import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;

public interface UserAppService {

    UserApp create(UserAppDto userAppDto);

}
