package oleborn.testresearch.repository;

import oleborn.testresearch.model.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAppRepository extends JpaRepository<UserApp, Long> {
}
