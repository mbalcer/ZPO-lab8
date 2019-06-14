package pl.mbalcer.managementsystem.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import lombok.Setter;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

public class TaskPanel {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;
    private Project project;
    private Sprint sprint;
    private SprintPanel sprintPanel;

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAllService(AllService allService) {
        this.allService = allService;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public void setSprintPanel(SprintPanel sprintPanel) {
        this.sprintPanel = sprintPanel;
    }

    public VerticalLayout getLayout() {
        VerticalLayout taskLayout = new VerticalLayout();

        Label name = new Label();
        name.setValue(sprint.toString());

        taskLayout.addComponent(name);
        return taskLayout;
    }
}
