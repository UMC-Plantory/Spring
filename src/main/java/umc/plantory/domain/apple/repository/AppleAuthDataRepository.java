package umc.plantory.domain.apple.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.apple.entity.AppleAuthData;

import java.util.Optional;

public interface AppleAuthDataRepository extends JpaRepository<AppleAuthData, Long> {
    Optional<AppleAuthData> findByTag (String tag);
}
