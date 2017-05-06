package etsa02;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {


    int POPUP_HEIGHT = 300;
    int POPUP_WIDTH = 300;

    int LOGIN_HEIGHT = 400;
    int LOGIN_WIDTH = 800;

    int MAIN_HEIGHT = 200;
    int MAIN_WIDTH = 200;

    public static void main(String args[])
    {
        launch(args);
    }

    Scene login_scene, main_scene;
    Stage stage_main;

    public void enterHandler(KeyEvent key, Button button) {
        if (key.getCode() == KeyCode.ENTER) {
            button.fire();
        }
    }

    public void dialogHandler(ActionEvent e, Text output) {

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("New User");

        dialog.setWidth(POPUP_WIDTH);
        dialog.setHeight(POPUP_HEIGHT);
        dialog.setMinWidth(POPUP_WIDTH);
        dialog.setMinHeight(POPUP_HEIGHT);
        dialog.setResizable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, POPUP_WIDTH, POPUP_HEIGHT);
        Label label_name = new Label("Name");
        TextField field_name = new TextField();
        grid.add(label_name, 0, 0);
        grid.add(field_name, 1, 0);

        Label label_ssn = new Label("SSN");
        TextField field_ssn = new TextField();
        grid.add(label_ssn, 0, 1);
        grid.add(field_ssn, 1, 1);

        Label label_address = new Label("Address");
        TextField field_address = new TextField();
        grid.add(label_address, 0, 2);
        grid.add(field_address, 1, 2);

        Label label_phone = new Label("Phone");
        TextField field_phone = new TextField();
        grid.add(label_phone, 0, 3);
        grid.add(field_phone, 1, 3);

        Label label_email = new Label("Email");
        TextField field_email = new TextField();
        grid.add(label_email, 0, 4);
        grid.add(field_email, 1, 4);

        Button ok = new Button("Ok");
        Button cancel = new Button("Cancel");

        grid.add(cancel, 0, 6);
        grid.add(ok, 1, 6);

        dialog.setScene(scene);

        scene.getStylesheets().add(Main.class.getResource("login.css").toExternalForm());

        dialog.show();
    }

    public void set_style(Scene scene)
    {
        String resource = Main.class.getResource("login.css").toExternalForm();
        scene.getStylesheets().add(resource);
    }

    public Scene setup_login_scene(Stage stage_main) {


        GridPane grid = get_grid();

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");

        Scene login_scene = new Scene(grid, MAIN_WIDTH, MAIN_HEIGHT);
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label password = new Label("Password");
        grid.add(password, 0, 2);

        PasswordField passwordBox = new PasswordField();
        grid.add(passwordBox, 1, 2);


        Button signIn = new Button("Sign in");
        HBox hbButton = new HBox(10);
        hbButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbButton.getChildren().add(signIn);
        grid.add(hbButton, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");


        /* Set up listener that fire the sign in button if ENTER is pressed in
         * any text field and the button. */
        userTextField.setOnKeyPressed(e-> enterHandler(e, signIn));
        passwordBox.setOnKeyPressed(e-> enterHandler(e, signIn));
        signIn.setOnKeyPressed(e-> enterHandler(e, signIn));

        String str_user = "a";
        String str_password = "a";

        signIn.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {
                String inp_user = userTextField.getText();
                String inp_password= passwordBox.getText();

                if (!inp_user.equals(str_user) || !inp_password.equals(str_password)) {
                    actiontarget.setText("Wrong password/user.");
                } else {
                    actiontarget.setText("Welcome "+ inp_user + ".");
                    stage_main.setScene(main_scene);
                    stage_main.show();
                }
            }
        });

        Button dialog = new Button("Add user");
        grid.add(dialog, 0, 10);
        dialog.setOnAction(e-> dialogHandler(e, actiontarget));

        set_style(login_scene);

        return login_scene;
    }

    public Scene setup_main_scene(Stage stage_main) {

        GridPane grid = get_grid();
        Scene main_scene = new Scene(grid, MAIN_WIDTH, MAIN_HEIGHT);
        Text sceneTitle = new Text("MAIN");

        grid.add(sceneTitle, 0, 0);

        set_style(main_scene);
        return main_scene;

    }

    public GridPane get_grid()
    {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }

    public void start(Stage primaryStage)
    {
        stage_main = primaryStage;
        stage_main.setWidth(LOGIN_WIDTH);
        stage_main.setHeight(LOGIN_HEIGHT);
        stage_main.setMinWidth(LOGIN_WIDTH);
        stage_main.setMinHeight(LOGIN_HEIGHT);
        stage_main.setResizable(false);

        stage_main.setTitle("JavaFX Welcome");

        login_scene = setup_login_scene(stage_main);
        main_scene = setup_main_scene(stage_main);

        stage_main.setScene(login_scene);
        stage_main.show();
    }
}
