package oleborn.testresearch.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost")
public interface UserFeignClient {

    @GetMapping("users-id/{id}")
    Boolean getAccess(@PathVariable Long id);

}
