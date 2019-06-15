package pl.mbalcer.managementsystem.ui;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Setter;
import pl.mbalcer.managementsystem.model.entity.*;
import pl.mbalcer.managementsystem.service.AllService;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Setter
public class ProjectPanel  {
    private LoginPanel loginPanel;
    private User user;
    private AllService allService;

    private Grid<UserInProject> userGrid;
    private VerticalLayout projectLayout;

    public VerticalLayout getLayout() {
        projectLayout = new VerticalLayout();

        Label nameUser = new Label();
        nameUser.setValue("List of Projects for: "+user.getLogin());
        projectLayout.addComponent(nameUser);

        HorizontalLayout mainButtons = new HorizontalLayout();
        mainButtons.setMargin(true);

        Button backToLogin = new Button();
        backToLogin.setIcon(VaadinIcons.ARROW_BACKWARD);
        backToLogin.addClickListener(event -> loginPanel.getUI().setContent(loginPanel.getLoginPanelLayout()));
        Button btnAddProject = initBtnAddProject();

        mainButtons.addComponents(backToLogin, btnAddProject);
        projectLayout.addComponent(mainButtons);

        List<Project> listProjects = allService.getProjectService().getAllProjectsByLeader(user);
        listProjects.addAll(allService.getUserInProjectService().getAllProjectsByUser(user));
        listProjects.stream()
                .forEach(project -> projectLayout.addComponent(initOneProject(project)));

        projectLayout.setComponentAlignment(nameUser, Alignment.MIDDLE_CENTER);
        return projectLayout;
    }

    private void updateUserGrid(Project project) {
        userGrid.setItems(allService.getUserInProjectService().getAllUsersByProject(project));
    }

    private VerticalLayout initOneProject(Project project) {
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

        HorizontalLayout btnLayout = new HorizontalLayout();
        Button btnShowSprint = initBtnSprint(project);
        btnLayout.addComponent(btnShowSprint);

        if (project.getLeader().equals(user)) {
            Button btnDelete = initBtnDelete(project, oneProjectLayout);
            btnLayout.addComponent(btnDelete);
        }
        oneProjectLayout.addComponent(btnLayout);

        oneProjectLayout.addLayoutClickListener(event -> {
            if (project.getLeader().equals(user)) {
                Window window = new Window(project.getName());
                window.setModal(true);
                window.setResizable(false);
                window.center();
                VerticalLayout vlWindow = new VerticalLayout();

                List<UserInProject> userListInProject = allService.getUserInProjectService().getAllUsersByProject(project);

                if(userListInProject.size() == 0) {
                    Label emptyUsers = new Label();
                    emptyUsers.setValue("No users participating in the project");
                    vlWindow.addComponent(emptyUsers);
                } else {
                    initGridUser(project, userListInProject);
                    vlWindow.addComponent(userGrid);
                }

                HorizontalLayout addUserToProjectLayout = new HorizontalLayout();
                Label emailLabel = new Label("Email");
                TextField emailUser = new TextField();

                Button addUserToProject = new Button();
                addUserToProject.setIcon(VaadinIcons.PLUS);
                addUserToProject.setStyleName(ValoTheme.BUTTON_PRIMARY);
                addUserToProject.addClickListener(add -> {
                    User addUser = allService.getUserService().getUserByEmail(emailUser.getValue());
                    if (addUser == null)
                        Notification.show("There isn't user with this email address", Notification.Type.ERROR_MESSAGE);
                    else if (!(userListInProject.stream().filter(u -> u.getUser().equals(addUser)).collect(Collectors.toList()).isEmpty()))
                        Notification.show("This user is already part of this project", Notification.Type.ERROR_MESSAGE);
                    else {
                        allService.getUserInProjectService().createUserInProject(new UserInProject(0l, addUser, project));
                        Notification.show("User has been added to the project", Notification.Type.TRAY_NOTIFICATION);
                        if (userListInProject.size()==0) {
                            vlWindow.removeAllComponents();
                            initGridUser(project, userListInProject);
                            vlWindow.addComponents(userGrid, addUserToProjectLayout);
                        }
                        updateUserGrid(project);
                        emailUser.clear();
                    }
                });

                addUserToProjectLayout.addComponents(emailLabel,emailUser, addUserToProject);

                vlWindow.addComponent(addUserToProjectLayout);
                window.setContent(vlWindow);
                LoginPanel.getCurrent().addWindow(window);
            } else
                Notification.show("You aren't the leader of this project so you can not manage it", Notification.Type.TRAY_NOTIFICATION);
        });

        return oneProjectLayout;
    }

    private void initGridUser(Project project, List<UserInProject> userListInProject) {
        userGrid = new Grid<>("List users in project");
        userGrid.setItems(userListInProject);
        userGrid.addColumn(u-> u.getUser().getLogin()).setCaption("Login");
        userGrid.addColumn(u-> u.getUser().getEmail()).setCaption("Email");
        userGrid.addColumn(c -> "Delete",
                new ButtonRenderer<>(btn -> {
                    allService.getUserInProjectService().deleteUserInProject(btn.getItem());
                    Notification.show("User has been removed from the project", Notification.Type.TRAY_NOTIFICATION);
                    updateUserGrid(project);
                }));
    }

    private Button initBtnAddProject() {
        Button addNewProject = new Button("Add new project");
        addNewProject.setStyleName(ValoTheme.BUTTON_PRIMARY);
        addNewProject.addClickListener(event -> {
            Window windowAddProject = new Window("Add new project");
            windowAddProject.setWidth(400.0f, Sizeable.Unit.PIXELS);
            windowAddProject.setModal(true);
            windowAddProject.setResizable(false);
            windowAddProject.center();
            FormLayout form = new FormLayout();
            form.setMargin(true);

            TextField titleProject = new TextField("Title");
            TextArea descriptionProject = new TextArea("Descrioption");
            Button btnAdd = new Button("Add");
            btnAdd.addClickListener(add -> {
                if (titleProject.getValue().isEmpty())
                    Notification.show("Title can't be empty", Notification.Type.ERROR_MESSAGE);
                else {
                    Project newProject = new Project(0l, titleProject.getValue(), descriptionProject.getValue(), user);
                    newProject = allService.getProjectService().createProject(newProject);
                    Notification.show("The new project has been successfully added");
                    projectLayout.addComponent(initOneProject(newProject));
                    windowAddProject.close();
                }
            });

            form.addComponents(titleProject, descriptionProject, btnAdd);
            windowAddProject.setContent(form);
            LoginPanel.getCurrent().addWindow(windowAddProject);
        });

        return addNewProject;
    }

    private Button initBtnSprint(Project project) {
        Button showSprint = new Button("Show sprint for "+project.getName());
        showSprint.addClickListener(event -> {
            SprintPanel sprintPanel = new SprintPanel();
            sprintPanel.setAllService(allService);
            sprintPanel.setLoginPanel(loginPanel);
            sprintPanel.setProject(project);
            sprintPanel.setUser(user);
            sprintPanel.setProjectPanel(this);

            loginPanel.getUI().setContent(sprintPanel.getLayout());
        });
        return showSprint;
    }

    private Button initBtnDelete(Project project, VerticalLayout oneProjectLayout) {
        Button deleteProject = new Button();
        deleteProject.setIcon(VaadinIcons.TRASH);
        deleteProject.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteProject.addClickListener(event -> {
            allService.getSprintService()
                    .getAllSprintByProject(project)
                    .stream()
                    .forEach(sprint -> deleteSprint(sprint));
            allService.getUserInProjectService().deleteAllUserByProject(project);
            allService.getProjectService().deleteProject(project.getId());
            projectLayout.removeComponent(oneProjectLayout);
            Notification.show("Project was successfully deleted", Notification.Type.TRAY_NOTIFICATION);
        });
        return deleteProject;
    }

    public void deleteSprint(Sprint sprint) {
        List<Task> sprintTaskList = allService.getTaskService().getAllTaskBySprint(sprint);
        sprintTaskList.stream()
                .forEach(task -> allService.getTaskService().deleteTask(task));

        allService.getSprintService().deleteSprint(sprint);
    }
}