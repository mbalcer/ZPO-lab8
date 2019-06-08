package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.model.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
