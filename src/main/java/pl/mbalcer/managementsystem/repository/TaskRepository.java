package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.Task;
import pl.mbalcer.managementsystem.model.enumType.Progress;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllBySprint(Sprint sprint);
    List<Task> findAllBySprintAndProgress(Sprint sprint, Progress progress);
}
