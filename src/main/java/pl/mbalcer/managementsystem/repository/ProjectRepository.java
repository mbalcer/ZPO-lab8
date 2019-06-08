package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.model.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
