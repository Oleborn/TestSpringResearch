package oleborn.testresearch.unittest;

import oleborn.testresearch.mapper.UserAppMapperImpl;
import oleborn.testresearch.model.dto.UserAppDto;
import oleborn.testresearch.model.entity.AuthUserApp;
import oleborn.testresearch.model.entity.UserApp;
import oleborn.testresearch.repository.UserAppRepository;
import oleborn.testresearch.service.UserAppService;
import oleborn.testresearch.service.UserAppServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                UserAppServiceImpl.class,
                UserAppMapperImpl.class
        }
)
public class UserServiceTest {

        @MockitoBean
        private UserAppRepository userAppRepository;

        @Autowired
        private UserAppService userAppService;

        @Captor
        private ArgumentCaptor<UserApp> userAppCaptor;

        @Test
        public void createUserAppTest(){

                UserAppDto userAppDto = new UserAppDto("Den", "test@mail.ru");
                UserApp userApp = new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp());

                Mockito.when(userAppRepository.save(any(UserApp.class))).thenReturn(userApp);

                UserApp savedUserApp = userAppService.create(userAppDto);

                Assertions.assertNotNull(savedUserApp, "createUserAppTest() returned null");
                Assertions.assertEquals(userAppDto.name(), savedUserApp.getName());
                Assertions.assertEquals(userAppDto.mail(), savedUserApp.getMail());

                verify(userAppRepository).save(userAppCaptor.capture());

                UserApp capturedUserApp = userAppCaptor.getValue();

                assertThat(capturedUserApp.getMail()).isEqualTo(userAppDto.mail());

        }

        @Test
        public void createUserAppTestWithException(){

                UserAppDto userAppDto = new UserAppDto("Den", "test@mail.ru");
                UserApp userApp = new UserApp(123L, "Den", "test@mail.ru", new AuthUserApp());

                Mockito.when(userAppRepository.findById(anyLong())).thenReturn(Optional.of(userApp));

                Assertions.assertThrows(
                        RuntimeException.class, () -> userAppService.create(userAppDto)
                );
        }

}
