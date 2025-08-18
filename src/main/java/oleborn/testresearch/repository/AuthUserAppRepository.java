package oleborn.testresearch.repository;

import oleborn.testresearch.model.entity.AuthUserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserAppRepository extends JpaRepository<AuthUserApp, Long> {
    Optional<AuthUserApp> findByMailAndPassword(String mail, String password);
}
