package pl.mbalcer.managementsystem.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Setter;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

import java.util.List;
import java.util.stream.Collectors;

@Setter
public class SprintPanel {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;
    private Project project;
    private ProjectPanel projectPanel;

    private VerticalLayout sprintLayout;
    private Grid<Sprint> sprintGrid;

    public VerticalLayout getLayout() {
        sprintLayout = new VerticalLayout();

        Label titleLabel = new Label();
        titleLabel.setValue("List of sprints for '"+project.getName()+"' project");

        HorizontalLayout mainButtons = new HorizontalLayout();
        Button btnBack = initBtnBack();
        mainButtons.addComponent(btnBack);

        if (project.getLeader().equals(user)) {
            Button btnAddSprint = initBtnAddSprint();
            mainButtons.addComponent(btnAddSprint);
        }

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
        btnAddSprint.addClickListener(event -> initWindowSprint(new Sprint()));

        return btnAddSprint;
    }

    private void initWindowSprint(Sprint editSprint) {
        Window windowAddSprint = new Window("Add/Edit new sprint");
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
        if (editSprint.getId() != null) {
            dateFrom.setValue(editSprint.getDateFrom());
            dateTo.setValue(editSprint.getDateTo());
            maxStoryPoints.setValue(String.valueOf(editSprint.getPlannedStoryPoints()));
            addSprint.setCaption("Edit");
        }

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

                if (editSprint.getId() != null)
                    sprints.remove(editSprint);

                if (sprints.size() == 0) {
                    if (editSprint.getId() != null) {
                        editSprint.setPlannedStoryPoints(plannedStoryPoints);
                        editSprint.setDateFrom(dateFrom.getValue().plusDays(1));
                        editSprint.setDateTo(dateTo.getValue().plusDays(1));
                        allService.getSprintService().updateSprint(editSprint);
                        Notification.show("Sprint has been successfully edited", Notification.Type.TRAY_NOTIFICATION);
                    } else  {
                        Sprint newSprint = new Sprint(0l, dateFrom.getValue().plusDays(1), dateTo.getValue().plusDays(1), plannedStoryPoints, project);
                        allService.getSprintService().createSprint(newSprint);
                        Notification.show("New sprint has been added", Notification.Type.TRAY_NOTIFICATION);
                    }
                    updateSprintTable();
                    windowAddSprint.close();
                } else
                    Notification.show("The sprint you want to add overlaps dates with another sprint", Notification.Type.ERROR_MESSAGE);

            } catch (NumberFormatException e) {
                Notification.show("Enter the correct number", Notification.Type.ERROR_MESSAGE);
            }
        });

        form.addComponents(dateFrom, dateTo, maxStoryPoints, addSprint);
        windowAddSprint.setContent(form);
        LoginPanel.getCurrent().addWindow(windowAddSprint);
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
        sprintGrid.setWidth(50.0f, Sizeable.Unit.PERCENTAGE);
        sprintGrid.addColumn(Sprint::getId).setCaption("##").setWidth(55.0);
        sprintGrid.addColumn(Sprint::getDateFrom).setCaption("Date from");
        sprintGrid.addColumn(Sprint::getDateTo).setCaption("Date to");
        sprintGrid.addColumn(Sprint::getPlannedStoryPoints).setCaption("Max story points");
        sprintGrid.addColumn(c -> "Show task",
                new ButtonRenderer<>(btn -> {
                    TaskPanel taskPanel = new TaskPanel();
                    taskPanel.setAllService(allService);
                    taskPanel.setLoginPanel(loginPanel);
                    taskPanel.setProject(project);
                    taskPanel.setUser(user);
                    taskPanel.setSprintPanel(this);
                    taskPanel.setSprint(btn.getItem());

                    loginPanel.getUI().setContent(taskPanel.getLayout());
                }));
        if (project.getLeader().equals(user)) {
            sprintGrid.addColumn(c -> "Delete",
                    new ButtonRenderer<>(btn -> {
                        allService.getSprintService().deleteSprint(btn.getItem());
                        Notification.show("Sprint has been removed from the project", Notification.Type.TRAY_NOTIFICATION);
                        updateSprintTable();
                    }));
        }

        sprintGrid.addItemClickListener(event -> initWindowSprint(event.getItem()));

        return sprintGrid;
    }

    private void updateSprintTable() {
        sprintGrid.setItems(allService.getSprintService().getAllSprintByProject(project));
    }
}
