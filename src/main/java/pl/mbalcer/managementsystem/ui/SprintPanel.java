package pl.mbalcer.managementsystem.ui;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.Sprint;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

import java.util.List;

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

        Label titleLabel = new Label();
        titleLabel.setValue("List of sprints for '"+project.getName()+"' project");

        Grid<Sprint> sprintGrid = initSprintTable();

        sprintLayout.addComponents(titleLabel, sprintGrid);
        sprintLayout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
        sprintLayout.setComponentAlignment(sprintGrid, Alignment.MIDDLE_CENTER);
        return sprintLayout;
    }

    private Grid<Sprint> initSprintTable() {
        Grid<Sprint> sprintGrid = new Grid<>();
        List<Sprint> sprintList = allService.getSprintService().getAllSprintByProject(project);
        sprintGrid.setItems(sprintList);
        sprintGrid.addColumn(Sprint::getId).setCaption("##");
        sprintGrid.addColumn(Sprint::getDateFrom).setCaption("Date from");
        sprintGrid.addColumn(Sprint::getDateTo).setCaption("Date to");
        sprintGrid.addColumn(Sprint::getPlannedStoryPoints).setCaption("Max story points");

        return sprintGrid;
    }
}
