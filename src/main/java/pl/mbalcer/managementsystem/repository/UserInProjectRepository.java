package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.entity.UserInProject;

public interface UserInProjectRepository extends JpaRepository<UserInProject, Long> {
}
