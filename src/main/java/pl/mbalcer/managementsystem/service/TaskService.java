package pl.mbalcer.managementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.Task;
import pl.mbalcer.managementsystem.model.enumType.Progress;
import pl.mbalcer.managementsystem.repository.SprintRepository;
import pl.mbalcer.managementsystem.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task getTask(Long id) {
        return taskRepository.getOne(id);
    }

    public List<Task> getAllTaskBySprint(Sprint sprint) {
        return taskRepository.findAllBySprint(sprint);
    }

    public List<Task> getAllTaskBySprintAndProgress(Sprint sprint, Progress progress) {
        return taskRepository.findAllBySprintAndProgress(sprint, progress);
    }

    public Task createTask(Task user) {
        return taskRepository.save(user);
    }

    public Task updateTask(Task user) {
        return taskRepository.save(user);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
