package umc.plantory.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.term.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByTermSort(String termSort);
    List<Term> findByIsRequiredTrue();
}
