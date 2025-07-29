package umc.plantory.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.plantory.domain.term.entity.Term;

import java.util.List;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findByIsRequiredTrue();
}