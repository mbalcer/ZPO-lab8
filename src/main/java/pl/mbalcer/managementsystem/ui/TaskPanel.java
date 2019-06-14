package pl.mbalcer.managementsystem.ui;

import com.vaadin.icons.VaadinIcons;
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

        HorizontalLayout mainButtons = new HorizontalLayout();
        Button btnBack = initBtnBack();
        Button btnAddTask = initBtnAddTask();
        mainButtons.addComponents(btnBack, btnAddTask);

        ComboBox<Sprint> sprintComboBox = getSprintComboBox();

        taskLayout.addComponents(projectName, mainButtons, sprintComboBox);
        taskLayout.setComponentAlignment(projectName, Alignment.TOP_CENTER);
        taskLayout.setComponentAlignment(mainButtons, Alignment.TOP_CENTER);
        taskLayout.setComponentAlignment(sprintComboBox, Alignment.TOP_CENTER);
        return taskLayout;
    }

    private Button initBtnAddTask() {
        Button btnAdd = new Button("Add new task");

        return btnAdd;
    }

    private Button initBtnBack() {
        Button btnBack = new Button();
        btnBack.setIcon(VaadinIcons.ARROW_BACKWARD);
        btnBack.addClickListener(event -> loginPanel.getUI().setContent(sprintPanel.getLayout()));
        return btnBack;
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
