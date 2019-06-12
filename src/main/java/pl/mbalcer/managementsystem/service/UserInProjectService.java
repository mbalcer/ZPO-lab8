package pl.mbalcer.managementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.model.entity.UserInProject;
import pl.mbalcer.managementsystem.repository.UserInProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInProjectService {
    @Autowired
    private UserInProjectRepository userInProjectRepository;

    public List<UserInProject> getAllUserInProject() {
        return userInProjectRepository.findAll();
    }

    public List<Project> getAllProjectsByUser(User user) {
        return userInProjectRepository.findAllByUser(user)
                .stream()
                .map(UserInProject::getProject)
                .collect(Collectors.toList());
    }

    public UserInProject createUserInProject(UserInProject project) {
        return userInProjectRepository.save(project);
    }

    public UserInProject updateUserInProject(UserInProject project) {
        return userInProjectRepository.save(project);
    }

    public void deleteUserInProject(Long id) {
        userInProjectRepository.deleteById(id);
    }

}
