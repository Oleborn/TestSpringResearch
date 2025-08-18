package oleborn.testresearch.repository;

import oleborn.testresearch.model.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAppRepository extends JpaRepository<UserApp, Long> {
    boolean existsByMail(String mail);

    Optional<UserApp> findByMail(String mail);
}
