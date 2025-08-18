package oleborn.testresearch.moduletest.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import oleborn.testresearch.exception.AccessDeniedException;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;

@Getter
@RequiredArgsConstructor
public enum EnumCase {

    SUCCESS_CASE(
            new UserAppDto("Den", "test@mail.ru"),
            new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()),
            true,
            201
    ),
    VALID_CASE(
            new UserAppDto("Den", "testmail.ru"),
            new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()),
            true,
            400
    ),
    ACCESS_CASE(
            new UserAppDto("Den", "testmail.ru"),
            new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp()),
            false,
            403
    );

    private final UserAppDto userAppDto;
    private final UserApp userApp;
    private final Boolean isAccess;

    private final int statusCode;
}
