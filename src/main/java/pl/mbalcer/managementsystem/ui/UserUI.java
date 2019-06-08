package pl.mbalcer.managementsystem.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import pl.mbalcer.managementsystem.model.entity.User;
import pl.mbalcer.managementsystem.service.UserService;

import java.util.Arrays;

@SpringUI(path = "")
public class UserUI extends UI {

    @Autowired
    private UserService userService;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
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
           // Loging
           Notification.show("Zalogowany", Notification.Type.TRAY_NOTIFICATION);
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
            else {
                User newUser = new User(0l, nameRegister.getValue(), passwordRegister.getValue(), email.getValue());
                newUser = userService.createUser(newUser);
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
        setContent(root);
    }

    private void clearField(AbstractTextField... components) {
        Arrays.stream(components)
                .forEach(c -> c.setValue(""));
    }

}
