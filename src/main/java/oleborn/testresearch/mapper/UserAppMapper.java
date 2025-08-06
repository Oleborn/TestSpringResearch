package oleborn.testresearch.mapper;

import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.UserApp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAppMapper {

    UserAppDto toUserAppDto(UserApp userApp);

    UserApp fromUserAppDto(UserAppDto userAppDto);

}
