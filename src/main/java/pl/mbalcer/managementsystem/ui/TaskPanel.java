package pl.mbalcer.managementsystem.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.components.grid.GridRowDragger;
import lombok.Setter;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.Task;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.model.enumType.Progress;
import pl.mbalcer.managementsystem.service.AllService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TaskPanel {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;
    private Project project;
    private Sprint sprint;
    private SprintPanel sprintPanel;

    private Task acctualTask;

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

        HorizontalLayout taskTables = initTaskTablesView();

        taskLayout.addComponents(projectName, mainButtons, sprintComboBox, taskTables);
        taskLayout.setComponentAlignment(projectName, Alignment.TOP_CENTER);
        taskLayout.setComponentAlignment(mainButtons, Alignment.TOP_CENTER);
        taskLayout.setComponentAlignment(sprintComboBox, Alignment.TOP_CENTER);
        return taskLayout;
    }

    private HorizontalLayout initTaskTablesView() {
        HorizontalLayout taskTables = new HorizontalLayout();
        List<Progress> progressList = Arrays.asList(Progress.BACKLOG, Progress.TO_DO, Progress.IN_PROGRESS, Progress.QA, Progress.DONE);
        progressList.stream()
                .forEach(progress -> {
                    Grid<Task> taskGrid = new Grid<>(progress.name());
                    taskGrid.setWidth(290.0f, Sizeable.Unit.PIXELS);
                    List<Task> taskList = allService.getTaskService().getAllTaskBySprintAndProgress(sprint, progress);
                    taskGrid.setItems(taskList);

                    taskGrid.addColumn(Task::getName).setCaption("Name");
                    taskGrid.addColumn(t -> t.getUser().getLogin()).setCaption("User");

                    GridDragSource<Task> source = new GridDragSource<>(taskGrid);
                    source.addGridDragStartListener(e -> {
                       acctualTask = e.getDraggedItems().iterator().next();
                       taskList.remove(acctualTask);
                       taskGrid.setItems(taskList);
                    });

                    GridDropTarget<Task> target = new GridDropTarget<>(taskGrid, DropMode.ON_TOP_OR_BETWEEN);
                    target.addGridDropListener(e -> {
                        acctualTask.setProgress(progress);
                        allService.getTaskService().updateTask(acctualTask);
                        taskList.add(acctualTask);
                        taskGrid.setItems(taskList);
                    });

                    taskTables.addComponent(taskGrid);
                });

        return taskTables;
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
