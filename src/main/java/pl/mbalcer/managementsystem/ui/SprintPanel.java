package pl.mbalcer.managementsystem.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

import java.util.List;
import java.util.stream.Collectors;

public class SprintPanel {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;
    private Project project;
    private ProjectPanel projectPanel;

    private VerticalLayout sprintLayout;
    private Grid<Sprint> sprintGrid;

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

    public void setProjectPanel(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
    }

    public VerticalLayout getLayout() {
        sprintLayout = new VerticalLayout();

        Label titleLabel = new Label();
        titleLabel.setValue("List of sprints for '"+project.getName()+"' project");

        HorizontalLayout mainButtons = new HorizontalLayout();
        Button btnBack = initBtnBack();
        Button btnAddSprint = initBtnAddSprint();
        mainButtons.addComponents(btnBack, btnAddSprint);

        Grid<Sprint> sprintGrid = initSprintTable();

        sprintLayout.addComponents(titleLabel, mainButtons, sprintGrid);
        sprintLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
        sprintLayout.setComponentAlignment(mainButtons, Alignment.MIDDLE_CENTER);
        sprintLayout.setComponentAlignment(sprintGrid, Alignment.MIDDLE_CENTER);
        return sprintLayout;
    }

    private Button initBtnAddSprint() {
        Button btnAddSprint = new Button("Add new sprint");
        btnAddSprint.setStyleName(ValoTheme.BUTTON_PRIMARY);
        btnAddSprint.addClickListener(event -> {
            Window windowAddSprint = new Window("Add new sprint");
            windowAddSprint.setWidth(400.0f, Sizeable.Unit.PIXELS);
            windowAddSprint.setModal(true);
            windowAddSprint.setResizable(false);
            windowAddSprint.center();

            FormLayout form = new FormLayout();
            form.setMargin(true);

            DateField dateFrom = new DateField("Date from");
            DateField dateTo = new DateField("Date to");
            TextField maxStoryPoints = new TextField("Max story points");
            Button addSprint = new Button("Add");
            addSprint.setStyleName(ValoTheme.BUTTON_PRIMARY);
            addSprint.addClickListener(add -> {
                try {
                    Integer plannedStoryPoints = Integer.parseInt(maxStoryPoints.getValue());
                    List<Sprint> sprints = allService.getSprintService()
                            .getAllSprintByProject(project)
                            .stream()
                            .filter(r -> (dateFrom.getValue().isBefore(r.getDateTo()) && dateFrom.getValue().isAfter(r.getDateFrom().minusDays(1)))
                                    || (dateTo.getValue().isAfter(r.getDateFrom()) && dateTo.getValue().isBefore(r.getDateTo())))
                            .collect(Collectors.toList());
                    if (sprints.size() == 0) {
                        Sprint newSprint = new Sprint(0l, dateFrom.getValue(), dateTo.getValue(), plannedStoryPoints, project);
                        newSprint = allService.getSprintService().createSprint(newSprint);
                        updateSprintTable();
                        windowAddSprint.close();
                        Notification.show("New sprint has been added", Notification.Type.TRAY_NOTIFICATION);
                    } else
                        Notification.show("The sprint you want to add overlaps dates with another sprint", Notification.Type.ERROR_MESSAGE);

                } catch (NumberFormatException e) {
                    Notification.show("Enter the correct number", Notification.Type.ERROR_MESSAGE);
                }
            });

            form.addComponents(dateFrom, dateTo, maxStoryPoints, addSprint);
            windowAddSprint.setContent(form);
            LoginPanel.getCurrent().addWindow(windowAddSprint);
        });

        return btnAddSprint;
    }

    private Button initBtnBack() {
        Button btnBack = new Button();
        btnBack.setIcon(VaadinIcons.ARROW_BACKWARD);
        btnBack.addClickListener(event -> loginPanel.getUI().setContent(projectPanel.getLayout()));

        return btnBack;
    }

    private Grid<Sprint> initSprintTable() {
        sprintGrid = new Grid<>();
        List<Sprint> sprintList = allService.getSprintService().getAllSprintByProject(project);
        sprintGrid.setItems(sprintList);
        sprintGrid.addColumn(Sprint::getId).setCaption("##");
        sprintGrid.addColumn(Sprint::getDateFrom).setCaption("Date from");
        sprintGrid.addColumn(Sprint::getDateTo).setCaption("Date to");
        sprintGrid.addColumn(Sprint::getPlannedStoryPoints).setCaption("Max story points");

        return sprintGrid;
    }

    private void updateSprintTable() {
        sprintGrid.setItems(allService.getSprintService().getAllSprintByProject(project));
    }
}
