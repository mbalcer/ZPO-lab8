package pl.mbalcer.managementsystem.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@Service
public class AllService {

    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserInProjectService userInProjectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private TaskService taskService;
}
