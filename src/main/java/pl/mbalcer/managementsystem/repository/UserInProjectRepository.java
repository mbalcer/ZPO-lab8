package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.model.entity.UserInProject;

import java.util.List;

public interface UserInProjectRepository extends JpaRepository<UserInProject, Long> {
    List<UserInProject> findAllByUser(User user);
    List<UserInProject> findAllByProject(Project project);
    @Transactional
    Long removeByProject(Project project);
}
