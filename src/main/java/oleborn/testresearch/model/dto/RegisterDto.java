package oleborn.testresearch.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterDto(
        String name,
        String mail,

        @Size(min = 2, max = 50, message = "Длинна пароля не соответствует")
        String password
) {
}
