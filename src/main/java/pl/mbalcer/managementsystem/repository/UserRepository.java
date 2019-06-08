package pl.mbalcer.managementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mbalcer.managementsystem.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
