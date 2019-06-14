package pl.mbalcer.managementsystem.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
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

        Label projectName = new Label();
        projectName.setValue("Project: "+project.getName());

        ComboBox<Sprint> sprintComboBox = getSprintComboBox();

        taskLayout.addComponents(projectName, sprintComboBox);
        taskLayout.setComponentAlignment(projectName, Alignment.TOP_CENTER);
        taskLayout.setComponentAlignment(sprintComboBox, Alignment.TOP_CENTER);
        return taskLayout;
    }

    private ComboBox<Sprint> getSprintComboBox() {
        ComboBox<Sprint> sprintComboBox = new ComboBox<>("Sprint");
        sprintComboBox.setItems(allService.getSprintService().getAllSprintByProject(project));
        sprintComboBox.setEmptySelectionAllowed(false);
        sprintComboBox.setItemCaptionGenerator(Sprint::toString);
        sprintComboBox.setValue(sprint);
        sprintComboBox.setWidth(30.0f, Sizeable.Unit.PERCENTAGE);
        sprintComboBox.addValueChangeListener(event -> {
            this.sprint = event.getValue();
            loginPanel.getUI().setContent(getLayout());
        });
        return sprintComboBox;
    }


}
