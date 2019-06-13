package pl.mbalcer.managementsystem.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.AllService;

import java.util.Arrays;

@SpringUI(path = "")
public class LoginPanel extends UI {

    @Autowired
    private AllService allService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout root = getLoginPanelLayout();
        setContent(root);
    }

    public VerticalLayout getLoginPanelLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();

        TabSheet mainTabSheet = new TabSheet();
        mainTabSheet.setHeight(100.0f, Unit.PERCENTAGE);
        mainTabSheet.setStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);

        //TAB Login
        FormLayout formLogin = new FormLayout();

        TextField name = new TextField("Login");
        PasswordField password = new PasswordField("Password");
        Button signIn = new Button("Sign in");
        signIn.addClickListener(event -> {
            if (allService.getUserService().getUserByLogin(name.getValue()) == null)
                Notification.show("There isn't user with a given login", Notification.Type.TRAY_NOTIFICATION);
            else {
                User loginUser = allService.getUserService().getUserByLogin(name.getValue());
                if (loginUser.getPassword().equals(password.getValue())) {
                    Notification.show(loginUser.getLogin()+" is logged on", Notification.Type.TRAY_NOTIFICATION);
                    clearField(name, password);

                    ProjectPanel projectPanel = new ProjectPanel();
                    projectPanel.setLoginPanel(this);
                    projectPanel.setUser(loginUser);
                    projectPanel.setAllService(allService);
                    getUI().setContent(projectPanel.getLayout());
                } else
                    Notification.show("The password provided is incorrect", Notification.Type.TRAY_NOTIFICATION);
            }
        });

        formLogin.addComponents(name, password, signIn);
        mainTabSheet.addTab(formLogin, "Login");

        // TAB Register
        FormLayout formRegister = new FormLayout();
        formRegister.setWidth(100.0f, Unit.PERCENTAGE);

        TextField nameRegister = new TextField("Login");
        TextField email = new TextField("Email");
        PasswordField passwordRegister = new PasswordField("Password");
        PasswordField passwordRegisterRepeat = new PasswordField("Repeat password");

        Button signUp = new Button("Sign Up");
        signUp.addClickListener(event -> {
            //Register
            if (!passwordRegister.getValue().equals(passwordRegisterRepeat.getValue()))
                Notification.show("Passwords must be the same", Notification.Type.TRAY_NOTIFICATION);
            else if(nameRegister.getValue().length()<6)
                Notification.show("Login must be at least 6 characters long", Notification.Type.TRAY_NOTIFICATION);
            else if(passwordRegister.getValue().length()<6)
                Notification.show("Password must be at least 6 characters long", Notification.Type.TRAY_NOTIFICATION);
            else if(allService.getUserService().getUserByLogin(nameRegister.getValue()) != null)
                Notification.show("The user with this login already exists", Notification.Type.TRAY_NOTIFICATION);
            else if(allService.getUserService().getUserByEmail(email.getValue()) != null)
                Notification.show("The user with this email already exists", Notification.Type.TRAY_NOTIFICATION);
            else {
                User newUser = new User(0l, nameRegister.getValue(), passwordRegister.getValue(), email.getValue());
                newUser = allService.getUserService().createUser(newUser);
                if (newUser != null) {
                    Notification.show("User has been successfully registered", Notification.Type.TRAY_NOTIFICATION);
                    clearField(nameRegister, email, passwordRegister, passwordRegisterRepeat);
                }
                else
                    Notification.show("An error occurred during registration", Notification.Type.TRAY_NOTIFICATION);
            }
        });

        formRegister.addComponents(nameRegister, email, passwordRegister, passwordRegisterRepeat, signUp);
        mainTabSheet.addTab(formRegister, "Register");

        root.addComponent(mainTabSheet);
        return root;
    }

    private void clearField(AbstractTextField... components) {
        Arrays.stream(components)
                .forEach(c -> c.setValue(""));
    }

}
