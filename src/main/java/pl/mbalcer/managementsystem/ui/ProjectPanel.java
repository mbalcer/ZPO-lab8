package pl.mbalcer.managementsystem.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.mbalcer.managementsystem.model.entity.Project;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.model.entity.UserInProject;
import pl.mbalcer.managementsystem.repository.ProjectRepository;
import pl.mbalcer.managementsystem.service.ProjectService;
import pl.mbalcer.managementsystem.service.UserInProjectService;
import pl.mbalcer.managementsystem.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectPanel  {

    private LoginPanel loginPanel;
    private User user;
    private UserService userService;
    private ProjectService projectService;
    private UserInProjectService userInProjectService;
    private Grid<UserInProject> userGrid;
    private VerticalLayout projectLayout;

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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public VerticalLayout getLayout() {
        projectLayout = new VerticalLayout();

        Label nameUser = new Label();
        nameUser.setValue("List of Projects for: "+user.getLogin());
        projectLayout.addComponent(nameUser);

        Button btnAddProject = initBtnAddProject();
        projectLayout.addComponent(btnAddProject);

        List<Project> listProjects = projectService.getAllProjectsByLeader(user);
        listProjects.addAll(userInProjectService.getAllProjectsByUser(user));
        listProjects.stream()
                .forEach(project -> projectLayout.addComponent(initOneProject(project)));

        projectLayout.setComponentAlignment(nameUser, Alignment.MIDDLE_CENTER);
        return projectLayout;
    }

    private void updateUserGrid(Project project) {
        userGrid.setItems(userInProjectService.getAllUsersByProject(project));
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
        Button btnDelete = initBtnDelete(project);
        btnLayout.addComponents(btnShowSprint, btnDelete);

        oneProjectLayout.addComponent(btnLayout);

        oneProjectLayout.addLayoutClickListener(event -> {
            if (project.getLeader().equals(user)) {
                Window window = new Window(project.getName());
                window.setModal(true);
                window.setResizable(false);
                window.center();
                VerticalLayout vlWindow = new VerticalLayout();

                List<UserInProject> userListInProject = userInProjectService.getAllUsersByProject(project);

                if(userListInProject.size() == 0) {
                    Label emptyUsers = new Label();
                    emptyUsers.setValue("No users participating in the project");
                    vlWindow.addComponent(emptyUsers);
                } else {
                    userGrid = new Grid<>("List users in project");
                    userGrid.setItems(userListInProject);
                    userGrid.addColumn(u-> u.getUser().getLogin()).setCaption("Login");
                    userGrid.addColumn(u-> u.getUser().getEmail()).setCaption("Email");
                    userGrid.addColumn(c -> "Delete",
                            new ButtonRenderer<>(btn -> {
                                userInProjectService.deleteUserInProject(btn.getItem());
                                Notification.show("User has been removed from the project", Notification.Type.TRAY_NOTIFICATION);
                                updateUserGrid(project);
                            }));

                    vlWindow.addComponent(userGrid);
                }

                HorizontalLayout addUserToProjectLayout = new HorizontalLayout();
                Label emailLabel = new Label("Email");
                TextField emailUser = new TextField();

                Button addUserToProject = new Button();
                addUserToProject.setIcon(VaadinIcons.PLUS);
                addUserToProject.setStyleName(ValoTheme.BUTTON_PRIMARY);
                addUserToProject.addClickListener(add -> {
                    User addUser = userService.getUserByEmail(emailUser.getValue());
                    //TODO protection of adding a user if it is already in the project
                    if (addUser == null)
                        Notification.show("There isn't user with this email address", Notification.Type.ERROR_MESSAGE);
                    else {
                        userInProjectService.createUserInProject(new UserInProject(0l, addUser, project));
                        Notification.show("User has been added to the project", Notification.Type.TRAY_NOTIFICATION);
                        updateUserGrid(project);
                        emailUser.clear();
                    }
                });

                addUserToProjectLayout.addComponents(emailLabel,emailUser, addUserToProject);

                vlWindow.addComponent(addUserToProjectLayout);
                window.setContent(vlWindow);
                LoginPanel.getCurrent().addWindow(window);
            }
        });

        return oneProjectLayout;
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
                    projectService.createProject(newProject);
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

        });
        return showSprint;
    }

    private Button initBtnDelete(Project project) {
        Button deleteProject = new Button();
        deleteProject.setIcon(VaadinIcons.TRASH);
        deleteProject.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteProject.addClickListener(event -> {

        });

        return deleteProject;
    }

}
