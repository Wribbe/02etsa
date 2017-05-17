package javadoc;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import hardware_testdrivers.BarcodePrinterTestDriver;

//import hardware_testdrivers.BarcodePrinterTestDriver;

import java.util.LinkedList;

public class Main extends Application {

    TreeItem global_selected_owner = null;
    TreeItem global_selected_barcode = null;

    BarcodePrinterTestDriver printer;

    private static GUIAPI api;

    int POPUP_HEIGHT = 300;
    int POPUP_WIDTH = 300;

    int LOGIN_HEIGHT = 400;
    int LOGIN_WIDTH = 800;

    int MAIN_HEIGHT = 200;
    int MAIN_WIDTH = 200;

    TreeItem<ListElement> users;
    TreeView<ListElement> view_root;


    public static void main(String args[])
    {
        api = new Core();

        api.newBikeOwner("Agda", "1989-01-01", "Hem Agda", "0701234567", "Agda@email.se", "CODE1");
        api.newBikeOwner("Bosse", "1997-11-01", "Hem Bosse", "0701234724", "Bosse@email.se", "CODE2");
        api.newBikeOwner("Cicci", "2003-02-13", "Hem Cicci", "0701235043", "Cicci@email.se", "CODE3");

        launch(args);
    }

    Scene login_scene, main_scene;
    Stage stage_main;

    private class OurButton extends Button {
      public OurButton(String label) {
        super(label);
        this.setOnKeyPressed(e-> enterHandler(e, this));
      }
    }

    public void enterHandler(KeyEvent key, OurButton button) {
        if (key.getCode() == KeyCode.ENTER) {
            button.fire();
        }
    }

    private class OurTextField extends TextField {

      private OurButton button;

      public OurTextField(OurButton button) {
        super();
        this.setOnKeyPressed(e-> enterHandler(e, button));
      }
    }

    private abstract class PopupBase {

        protected final Stage dialog = new Stage();
        protected GridPane grid;
        protected Text output;

        protected List<OurTextField> fields = new ArrayList<OurTextField>();

        public PopupBase(String title, Text output) {

            this.output = output;

            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);

            dialog.setWidth(POPUP_WIDTH);
            dialog.setHeight(POPUP_HEIGHT);
            dialog.setMinWidth(POPUP_WIDTH);
            dialog.setMinHeight(POPUP_HEIGHT);
            dialog.setResizable(false);

            grid = get_grid();

            // Create ok and cancel button.
            OurButton ok = new OurButton("Ok");
            OurButton cancel = new OurButton("Cancel");

            // Set ok and cancel actions.
            ok.setOnAction(action_ok());
            cancel.setOnAction(action_cancel());

            grid.add(cancel, 0, 6);
            grid.add(ok, 1, 6);

            Scene scene = new Scene(grid, POPUP_WIDTH, POPUP_HEIGHT);

            Label label_name = new Label("Name");
            grid.add(label_name, 0, 0);
            fields.add(new OurTextField(ok));
            grid.add(fields.get(fields.size()-1), 1, 0);

            Label label_ssn = new Label("SSN");
            grid.add(label_ssn, 0, 1);
            fields.add(new OurTextField(ok));
            grid.add(fields.get(fields.size()-1), 1, 1);

            Label label_address = new Label("Address");
            grid.add(label_address, 0, 2);
            fields.add(new OurTextField(ok));
            grid.add(fields.get(fields.size()-1), 1, 2);

            Label label_phone = new Label("Phone");
            grid.add(label_phone, 0, 3);
            fields.add(new OurTextField(ok));
            grid.add(fields.get(fields.size()-1), 1, 3);

            Label label_email = new Label("Email");
            grid.add(label_email, 0, 4);
            fields.add(new OurTextField(ok));
            grid.add(fields.get(fields.size()-1), 1, 4);

            dialog.setScene(scene);
            scene.getStylesheets().add(Main.class.getResource("../etsa02/login.css").toExternalForm());

        }

        public String[] fieldsAsArray() {
            List<String> list = new ArrayList<String>();
            for (OurTextField field : fields) {
                list.add(field.getText().trim());
            }
            return list.toArray(new String[list.size()]);
        }

        public abstract void show();

        public abstract String status_ok();

        public abstract String status_cancel();

        public abstract EventHandler<ActionEvent> action_ok();
        public abstract EventHandler<ActionEvent> action_cancel();
    }

    private class PopupNewUser extends PopupBase {
        public PopupNewUser(Text output) {
            super("New User", output);
        }
        public void show() {
            this.dialog.show();
        }
        public String status_ok() {
            return "User created successfully.";
        }
        public String status_cancel() {
            return "User creation aborted.";
        }

        private boolean validate() {
            for (OurTextField field : fields) {
                if (field.getText().trim().equals("")) {
                    return false;
                }
            }
            return true;
        }

        public EventHandler<ActionEvent> action_ok() {
            EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (validate()) {
                        output.setText(status_ok());
                        api.newBikeOwner(fieldsAsArray());
                        update_view();
                        dialog.close();
                    }
                }
            };
            return handler;
        }
        public EventHandler<ActionEvent> action_cancel() {
            EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    output.setText(status_cancel());
                    dialog.close();
                }
            };
            return handler;
        }
    }

    public void add_user(BikeOwner user) {
//        users.getChildren().add(new TreeItem<ListElement>(user));
    }

    public void popup_handler(ActionEvent e, PopupBase popup) {
        popup.show();
    }

    public void set_style(Scene scene)
    {
        String resource = Main.class.getResource("../etsa02/login.css").toExternalForm();
        scene.getStylesheets().add(resource);
    }

    public Scene setup_login_scene(Stage stage_main) {

        GridPane grid = get_grid();

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");

        OurButton signIn = new OurButton("Sign in");
        HBox hbButton = new HBox(10);
        hbButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbButton.getChildren().add(signIn);
        grid.add(hbButton, 1, 4);

        Scene login_scene = new Scene(grid, MAIN_WIDTH, MAIN_HEIGHT);
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        OurTextField userTextField = new OurTextField(signIn);
        grid.add(userTextField, 1, 1);

        Label password = new Label("Password:");
        grid.add(password, 0, 2);

        PasswordField passwordBox = new PasswordField();
        passwordBox.setOnKeyPressed(e-> enterHandler(e, signIn));
        grid.add(passwordBox, 1, 2);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");

        String str_user = "admin";
        String str_password = "password";

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
                    //printer = new BarcodePrinterTestDriver("mupp", 10, 10);
                }
            }
        });

        set_style(login_scene);

        return login_scene;
    }

    public void update_view() {

        users = new TreeItem<ListElement>();

        for (BikeOwner owner : api.listUsers()) {
            TreeItem<ListElement> item = new TreeItem<ListElement>(owner, null);
            for (Barcode barcode : owner.getBarcodes()) {
                item.getChildren().add(new TreeItem<ListElement>(barcode, null));
            }
            users.getChildren().add(item);
        }
        view_root.setRoot(users);

    }

    public Scene setup_main_scene(Stage stage_main) {

        GridPane main_grid = get_grid();
        GridPane button_grid = get_grid(Pos.TOP_LEFT);
        GridPane list_grid = get_grid();

        Scene main_scene = new Scene(main_grid, MAIN_WIDTH, MAIN_HEIGHT);

        view_root = new TreeView<ListElement>();
        view_root.setShowRoot(false);

        ChangeListener users_listener = new ChangeListener<TreeItem<ListElement>>() {
            public void changed(ObservableValue<? extends TreeItem<ListElement>> observable,
                                TreeItem<ListElement> value_old, TreeItem<ListElement> value_new) {

                if(value_new != null) {
                    ListElement element = value_new.getValue();

                    if (element instanceof BikeOwner) {
                        global_selected_owner = value_new;
                        global_selected_barcode = null;
                    } else if (element instanceof Barcode) {
                        global_selected_owner = null;
                        global_selected_barcode = value_new;
                    } else {
                        global_selected_owner = null;
                        global_selected_barcode = null;
                        System.out.println("Don't know what you clicked.");
                    }
                }
            }
        };

        view_root.getSelectionModel().selectedItemProperty().addListener(users_listener);


        update_view();

        list_grid.add(view_root, 0, 0);

        // Create statusbar and label.
        Label status_label = new Label("Status:");
        Text status_bar = new Text();

        // Set buttons.
        OurButton button_new_user = new OurButton("New user");
        button_grid.add(button_new_user, 1, 0);
        PopupBase new_user = new PopupNewUser(status_bar);
        button_new_user.setOnAction(e-> popup_handler(e, new_user));

        OurButton button_edit_user = new OurButton("Edit user");
        button_grid.add(button_edit_user, 1, 1);

        OurButton button_remove_user = new OurButton("Remove user");
        button_grid.add(button_remove_user, 1, 2);
        button_remove_user.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_owner == null) {
                    status_bar.setText("Error: Please select a user.");
                    return;
                }
                BikeOwner to_be_removed = (BikeOwner) global_selected_owner.getValue();
                api.removeBikeOwner(to_be_removed);
                update_view();
                status_bar.setText("Successfully removed: "+to_be_removed.name()+".");
            }
        });

        OurButton button_add_barcode = new OurButton("Add barcode");
        button_grid.add(button_add_barcode, 1, 3);
        button_add_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_owner == null) {
                    status_bar.setText("Error: Please select a user.");
                    return;
                }
                System.out.println("ADD BARCODE");
                // Add barcode to user.
            }
        });

        OurButton button_remove_barcode = new OurButton("Remove barcode");
        button_grid.add(button_remove_barcode, 1, 4);
        button_remove_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_barcode == null) {
                    status_bar.setText("Error: Please select a barcode.");
                    return;
                }
                Barcode to_be_removed = (Barcode) global_selected_barcode.getValue();
                BikeOwner owner = (BikeOwner) global_selected_barcode.getParent().getValue();
                api.removeBarcode(owner, to_be_removed);
                update_view();
                status_bar.setText("Successfully removed: "+to_be_removed.toString()+" from: +"+owner.name()+".");
            }
        });

        OurButton button_print_barcode = new OurButton("Print barcode");
        button_grid.add(button_print_barcode, 1, 5);
        button_print_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                System.out.println("PRINT BARCODE!");
            }
        });

        // Add button grid to main grid.
        main_grid.add(button_grid, 1, 0);

        // Add list grid to main grid.
        main_grid.add(list_grid, 0, 0);

        // Add statusbar to main grid.
        GridPane status_grid = get_grid(Pos.TOP_LEFT);
        status_grid.add(status_label, 0, 0);
        status_grid.add(status_bar, 1, 0);
        main_grid.add(status_grid, 0, 1);

        // Set window style.
        set_style(main_scene);
        return main_scene;
    }

    public GridPane get_grid(Pos position)
    {
        GridPane grid = new GridPane();
        grid.setAlignment(position);

        grid.setHgap(2);
        grid.setVgap(10);
        grid.setPadding(new Insets(5, 5, 5, 5));
        return grid;
    }

    public GridPane get_grid()
    {
        return get_grid(Pos.CENTER);
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
