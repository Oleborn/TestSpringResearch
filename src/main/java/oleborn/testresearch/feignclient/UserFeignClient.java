package oleborn.testresearch.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${test_server.baseUrl}")
public interface UserFeignClient {

    @GetMapping("users-id/{mail}")
    Boolean getAccess(@PathVariable String mail);

}
