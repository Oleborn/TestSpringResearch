package oleborn.testresearch.model.dto;

import jakarta.validation.constraints.Email;

public record UserAppDto(

        String name,

        @Email
        String mail
) {
}
