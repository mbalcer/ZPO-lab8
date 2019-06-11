package pl.mbalcer.managementsystem.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import pl.mbalcer.managementsystem.model.entity.User;

public class ProjectPanel  {

    private LoginPanel loginPanel;
    private User user;

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public VerticalLayout getLayout() {
        VerticalLayout projectLayout = new VerticalLayout();

        Label nameUser = new Label();
        nameUser.setValue(user.getLogin());

        projectLayout.addComponent(nameUser);
        return projectLayout;
    }
}
