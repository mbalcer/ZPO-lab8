package pl.mbalcer.managementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.repository.SprintRepository;

import java.util.List;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    public Sprint getSprint(Long id) {
        return sprintRepository.getOne(id);
    }

    public List<Sprint> getAllSprintByProject(Project project) {
        return sprintRepository.findAllByProject(project);
    }

    public Sprint createSprint(Sprint user) {
        return sprintRepository.save(user);
    }

    public Sprint updateSprint(Sprint user) {
        return sprintRepository.save(user);
    }

    public void deleteSprint(Sprint sprint) {
        sprintRepository.delete(sprint);
    }

}
