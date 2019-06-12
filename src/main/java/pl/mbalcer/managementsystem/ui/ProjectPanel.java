package pl.mbalcer.managementsystem.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.repository.ProjectRepository;
import pl.mbalcer.managementsystem.service.ProjectService;
import pl.mbalcer.managementsystem.service.UserInProjectService;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectPanel  {

    private LoginPanel loginPanel;
    private User user;
    private ProjectService projectService;
    private UserInProjectService userInProjectService;

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void setUserInProjectService(UserInProjectService userInProjectService) {
        this.userInProjectService = userInProjectService;
    }

    public VerticalLayout getLayout() {
        VerticalLayout projectLayout = new VerticalLayout();

        Label nameUser = new Label();
        nameUser.setValue("List of Projects for: "+user.getLogin());
        projectLayout.addComponent(nameUser);

        List<Project> listProjects = projectService.getAllProjectsByLeader(user);
        listProjects.addAll(userInProjectService.getAllProjectsByUser(user));
        listProjects.stream()
                .forEach(project -> {
                    VerticalLayout oneProjectLayout = new VerticalLayout();
                    Label titleProject = new Label();
                    titleProject.setValue("Title: "+project.getName());

                    Label leaderProject = new Label();
                    leaderProject.setValue("Leader: "+project.getLeader().getLogin());

                    HorizontalLayout mainInformation = new HorizontalLayout();
                    mainInformation.addComponents(titleProject, leaderProject);
                    mainInformation.setComponentAlignment(titleProject, Alignment.MIDDLE_LEFT);
                    mainInformation.setComponentAlignment(leaderProject, Alignment.MIDDLE_RIGHT);

                    Label descriptionProject = new Label();
                    descriptionProject.setValue(project.getDescription());
                    oneProjectLayout.addComponents(mainInformation, descriptionProject);

                    oneProjectLayout.addLayoutClickListener(event -> {
                        Notification.show(project.toString(), Notification.Type.TRAY_NOTIFICATION);
                    });

                    projectLayout.addComponent(oneProjectLayout);
                    projectLayout.setComponentAlignment(oneProjectLayout, Alignment.BOTTOM_CENTER);
                });

        projectLayout.setComponentAlignment(nameUser, Alignment.MIDDLE_CENTER);
        return projectLayout;
    }
}
