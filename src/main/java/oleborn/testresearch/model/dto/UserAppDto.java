package oleborn.testresearch.model.dto;

import jakarta.validation.constraints.Email;

public record UserAppDto(
        long id,
        String name,

        @Email
        String mail
) {
}
