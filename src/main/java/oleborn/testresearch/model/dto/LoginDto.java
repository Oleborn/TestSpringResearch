package oleborn.testresearch.model.dto;

import lombok.Builder;

@Builder
public record LoginDto(
        String mail,
        String password
) {
}
