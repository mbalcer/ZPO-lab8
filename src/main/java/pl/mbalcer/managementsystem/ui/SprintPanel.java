package pl.mbalcer.managementsystem.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

public class SprintPanel {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;
    private Project project;
    private VerticalLayout sprintLayout;

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

    public VerticalLayout getLayout() {
        sprintLayout = new VerticalLayout();

        Label show = new Label("abc");
        
        sprintLayout.addComponent(show);

        return sprintLayout;
    }
}
