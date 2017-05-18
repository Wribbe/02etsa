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
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

import java.io.IOException;

import hardware_testdrivers.BarcodePrinterTestDriver;

//import hardware_testdrivers.BarcodePrinterTestDriver;

import java.util.LinkedList;

public class Main extends Application {

    TreeItem global_selected_owner = null;
    TreeItem global_selected_barcode = null;

    BarcodePrinterTestDriver printer;

    private static GUIAPI api;

    private String[] popup_fields = {"Name", "SSN", "Address", "Phone", "Email"};
    private String FORMAT_DATE = "DATEFORMAT";
    private String[] formats_popup = {".*", FORMAT_DATE, ".*", "\\d{9,10}", ".+@.+\\..+"};

    private final int MAX_BARCODES = 2;

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

        public PopupBase(String title, Text output, String... fields_input) {

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

            grid.add(cancel, 0, fields_input.length);
            grid.add(ok, 1, fields_input.length);

            Scene scene = new Scene(grid, POPUP_WIDTH, POPUP_HEIGHT);

            for (int i=0; i<fields_input.length; i++) {
                grid.add(new Label(fields_input[i]), 0, i);
                fields.add(new OurTextField(ok));
                grid.add(fields.get(fields.size()-1), 1, i);
            }

            dialog.setScene(scene);
            scene.getStylesheets().add(Main.class.getResource("../etsa02/login.css").toExternalForm());

        }

        public String[] fieldsAsArray() {
            List<String> list = new ArrayList<String>();
            // Pad pin with empty string to make ssn() return correct value.
            list.add("");
            for (OurTextField field : fields) {
                list.add(field.getText().trim());
            }
            return list.toArray(new String[list.size()]);
        }

        public String[] fieldsAsArray(String pin) {
            List<String> list = new ArrayList<String>();
            list.add(pin);
            for (OurTextField field : fields) {
                list.add(field.getText().trim());
            }
            return list.toArray(new String[list.size()]);
        }

        protected String validate() {
            int current_index = 0;
            for (OurTextField field : fields) {

                String field_name = popup_fields[current_index];
                String field_value = field.getText().trim();
                String format_string = formats_popup[current_index];

                if (field_value.equals("")) {
                  return "Please put a value in field \""+field_name+"\".";
                }
                if (format_string.equals(FORMAT_DATE)) {
                  if (!field_value.matches("\\d{12}")) {
                    return field_name+" needs to be 12 digits.";
                  }
                  Integer month = new Integer(field_value.substring(4,6));
                  Integer day = new Integer(field_value.substring(6,8));
                  if (month > 12 || month < 1) {
                    return field_name+" month needs to be between 1-12.";
                  }
                  if (day > 31 | day < 1) {
                    return field_name+" day needs to be between 1-31.";
                  }
                } else if(!field_value.matches(format_string)){
                  return field_name+" has wrong format.";
                }
                current_index++;
            }
            return "";
        }


        public abstract void show();

        public abstract String status_ok();

        public abstract String status_cancel();

        public abstract EventHandler<ActionEvent> action_ok();
        public abstract EventHandler<ActionEvent> action_cancel();
    }

    private class PopupNewUser extends PopupBase {
        public PopupNewUser(Text output, String... input_fields) {
            super("New User", output, input_fields);
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

        public EventHandler<ActionEvent> action_ok() {
            EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (validate().equals("")) {
                        output.setText(status_ok());
                        api.newBikeOwner(fieldsAsArray());
                        TreeItem<ListElement> item;
                        BikeOwner guiOwner = new BikeOwner(fieldsAsArray());
                        try {
                            String pin = api.pin(guiOwner.ssn());
                            api.setPin(guiOwner, pin);
                        } catch (IOException ioe) {
                            api.setPin(guiOwner, "");
                        }
                        item = new TreeItem<ListElement>(guiOwner, null);
                        users.getChildren().add(item);
                        users.getChildren().sort(Comparator.comparing(t->t.toString().toLowerCase()));
                        dialog.close();
                    } else {
                        output.setText(validate());
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

    private class PopupEditUser extends PopupBase {

        TreeItem<ListElement> owner;

        public PopupEditUser(Text output, TreeItem<ListElement> owner) {
            super("Edit User", output, popup_fields);
            this.owner = owner;
            String[] fields_owner = ((BikeOwner) owner.getValue()).getFields();
            for (int i=0; i<fields.size(); i++) {
                this.fields.get(i).setText(fields_owner[i+1]);
            }
        }
        public void show() {
            this.dialog.show();
        }
        public String status_ok() {
            return "User edited successfully.";
        }
        public String status_cancel() {
            return "User edit aborted.";
        }

        public EventHandler<ActionEvent> action_ok() {
            EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e) {
                    if (validate().equals("")) {
                        output.setText(status_ok());
                        BikeOwner bike_owner = (BikeOwner) owner.getValue();
                        BikeOwner updated = new BikeOwner(fieldsAsArray(bike_owner.pin()));
                        // Update database before updating GUI object.
                        api.editBikeOwner(bike_owner, updated);
                        // Update GUI object.
                        bike_owner.update(updated);
                        users.getChildren().sort(Comparator.comparing(t->t.toString().toLowerCase()));
                        dialog.close();
                    } else {
                        output.setText(validate());
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

                //if (!inp_user.equals(str_user) || !inp_password.equals(str_password)) {
                if (false) {
                    actiontarget.setText("Wrong password/user.");
                } else {
                    actiontarget.setText("Welcome "+ inp_user + ".");
                    stage_main.setScene(main_scene);
                    stage_main.show();
                }
            }
        });

        set_style(login_scene);
        userTextField.requestFocus();

        return login_scene;
    }

    private void handleMouseClicked(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        // Accept clicks only on node cells, and not on empty spaces of the TreeView
        if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
            TreeItem<ListElement> item = (TreeItem)view_root.getSelectionModel().getSelectedItem();
            ListElement element = item.getValue();
            if (element instanceof BikeOwner) {
                global_selected_owner = item;
                global_selected_barcode = null;
            } else if (element instanceof Barcode) {
                global_selected_owner = null;
                global_selected_barcode = item;
            } else {
                global_selected_owner = null;
                global_selected_barcode = null;
                System.out.println("Don't know what you clicked.");
            }
        }
    }

    private class BarcodeWrapper extends Barcode {

      public int number = 0;
      private Barcode barcode;

      public BarcodeWrapper(String string, int number) {
        super(string);
        this.number = number;
      }

      public BarcodeWrapper(Integer integer, int number) {
        super(integer);
        this.number = number;
      }

      public BarcodeWrapper(Barcode barcode, int number) {
        super(barcode.toString());
        this.number = number;
        this.barcode = barcode;
      }

      public String toString() {
        return "Bike "+number+": "+super.toString();
      }

    }

    public Scene setup_main_scene(Stage stage_main) {

        GridPane main_grid = get_grid();
        GridPane button_grid = get_grid(Pos.TOP_LEFT);
        GridPane list_grid = get_grid();

        Scene main_scene = new Scene(main_grid, MAIN_WIDTH, MAIN_HEIGHT);

        view_root = new TreeView<ListElement>();
        view_root.setShowRoot(false);

        EventHandler<MouseEvent> mouseEventHandle = (MouseEvent event) -> {
            handleMouseClicked(event);
        };

        view_root.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandle);


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

        list_grid.add(view_root, 0, 0);

        users = new TreeItem<ListElement>();

        for (BikeOwner owner : api.listUsers()) {
            TreeItem<ListElement> item = new TreeItem<ListElement>(owner, null);
            int current_index = 1;
            for (Barcode barcode : owner.getBarcodes()) {
                BarcodeWrapper wrapper = new BarcodeWrapper(barcode, current_index);
                item.getChildren().add(new TreeItem<ListElement>(wrapper, null));
                current_index++;
            }
            users.getChildren().add(item);
        }
        users.getChildren().sort(Comparator.comparing(t->t.toString().toLowerCase()));

        view_root.setRoot(users);

        // Create statusbar and label.
        Label status_label = new Label("Status:");
        Text bar_status = new Text();

        // Set buttons.
        OurButton button_new_user = new OurButton("New user");
        button_grid.add(button_new_user, 1, 0);
        button_new_user.setOnAction(e-> popup_handler(e,new PopupNewUser(bar_status, popup_fields)));

        OurButton button_edit_user = new OurButton("Edit user");
        button_grid.add(button_edit_user, 1, 1);
        button_edit_user.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_owner == null) {
                    bar_status.setText("Error: Please select a user.");
                    return;
                }
                popup_handler(e, new PopupEditUser(bar_status, global_selected_owner));
            }
        });

        OurButton button_remove_user = new OurButton("Remove user");
        button_grid.add(button_remove_user, 1, 2);
        button_remove_user.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_owner == null) {
                    bar_status.setText("Error: Please select a user.");
                    return;
                }
                // Remove from database.
                BikeOwner to_be_removed = (BikeOwner) global_selected_owner.getValue();
                api.removeBikeOwner(to_be_removed);

                // Remove from gui.
                global_selected_owner.getParent().getChildren().remove(global_selected_owner);
                global_selected_owner = null;
            }
        });

        OurButton button_add_barcode = new OurButton("Add barcode");
        button_grid.add(button_add_barcode, 1, 3);
        button_add_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_owner == null) {
                    bar_status.setText("Error: Please select a user.");
                    return;
                }
                if (global_selected_owner.getChildren().size() >= MAX_BARCODES) {
                    bar_status.setText("Maximum number of bikes registered.");
                    return;
                }

                TreeItem<ListElement> owner = global_selected_owner;
                Barcode new_code = api.newBarcode();
                int left = api.barcodesLeft();
                if (left > 0) {
                    api.addBarcode((BikeOwner) owner.getValue(), new_code);
                    int num_bikes = owner.getChildren().size()+1;
                    BarcodeWrapper wrapper = new BarcodeWrapper(new_code, num_bikes);
                    owner.getChildren().add(new TreeItem<ListElement>(wrapper, null));
                    //owner.getChildren().sort(Comparator.comparing(t->t.toString().toLowerCase()));
                }
                left = api.barcodesLeft();
                if (left == 0) {
                    bar_status.setText("Warning, no barcodes left!");
                } else if (left < 100) {
                    bar_status.setText("Warning, only: "+left+" barcodes left!");
                } else {
                    bar_status.setText(left+" barcodes left.");
                }
                return;
            }
        });

        OurButton button_remove_barcode = new OurButton("Remove barcode");
        button_grid.add(button_remove_barcode, 1, 4);
        button_remove_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_barcode == null) {
                    bar_status.setText("Error: Please select a barcode.");
                    return;
                }
                // Remove from core.
                Barcode to_be_removed = (Barcode) global_selected_barcode.getValue();
                BikeOwner owner = (BikeOwner) global_selected_barcode.getParent().getValue();
                api.removeBarcode(owner, to_be_removed);

                // Remove from gui.
                List<TreeItem<ListElement>> children = global_selected_barcode.getParent().getChildren();
                children.remove(global_selected_barcode);
                global_selected_barcode = null;

                // Update wrapper numbers.
                int current_index = 1;
                for (TreeItem<ListElement> child : children) {
                  BarcodeWrapper wrapper = (BarcodeWrapper)child.getValue();
                  wrapper.number = current_index;
                  current_index++;
                }

                bar_status.setText("Successfully removed: "+to_be_removed.toString());
            }
        });

        OurButton button_print_barcode = new OurButton("Print barcode");
        button_grid.add(button_print_barcode, 1, 5);
        button_print_barcode.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                if (global_selected_barcode == null) {
                    bar_status.setText("Error: Please select a barcode.");
                    return;
                }
                if (printer == null) {
                    printer = new BarcodePrinterTestDriver("Barcode Printer", 10, 10);
                }
                printer.printBarcode(((Barcode)global_selected_barcode.getValue()).toString());
            }
        });

        // Add button grid to main grid.
        main_grid.add(button_grid, 1, 0);

        // Add list grid to main grid.
        main_grid.add(list_grid, 0, 0);


        // Add search bar.
        GridPane search_grid = get_grid(Pos.TOP_LEFT);
        OurButton button_search = new OurButton("Search");
        OurTextField bar_search = new OurTextField(button_search);
        search_grid.add(bar_search, 0, 0);
        search_grid.add(button_search, 1, 0);
        main_grid.add(search_grid, 0, 1);

        // Setup search button.
        button_search.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                bar_status.setText("Search");
            }
        });

        // Create status grid.
        GridPane status_grid = get_grid(Pos.TOP_LEFT);

        // Add status bar.
        status_grid.add(status_label, 0, 0);
        status_grid.add(bar_status, 1, 0, 2, 1);

        // Add status_grid to main grid.
        main_grid.add(status_grid, 0, 2, 2, 1);

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
