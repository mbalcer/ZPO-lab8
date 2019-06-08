package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.model.entity.Sprint;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
}
