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


	public static void main(String args[]) 
	{
		launch(args);
	}
	
	GridPane grid1, grid2;
	Scene scene1, scene2;
	Stage stage_login;

	public void enterHandler(KeyEvent key, Button button) {
		if (key.getCode() == KeyCode.ENTER) {
			button.fire();
		}
	}

	public void dialogHandler(ActionEvent e, Text output) {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.WINDOW_MODAL);
		
		Button ok = new Button("Ok");
		Button cancel = new Button("Cancel");
		
		ok.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e) {
				output.setText("User added.");
				dialog.close();
			}
		});

		cancel.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e) {
				output.setText("Operation Canceled.");
				dialog.close();
			}
		});
		
		@SuppressWarnings("deprecation")
		Scene dialogScene = new Scene(VBoxBuilder.create()
				.children(new Text("Create user."), ok, cancel)
				.alignment(Pos.CENTER)
				.padding(new Insets(10))
				.build());
		dialog.setScene(dialogScene);
		dialog.show();
	}
	
	public void start(Stage primaryStage)
	{
		stage_login = primaryStage;

		stage_login.setTitle("JavaFX Welcome");
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);

		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		Text scenetitle = new Text("Welcome");
		scenetitle.setId("welcome-text");
		
		Scene scene = new Scene(grid, 300, 275);
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
				}
			}
		});
		
		Button dialog = new Button("Add user");
		grid.add(dialog, 0, 10);
		dialog.setOnAction(e-> dialogHandler(e, actiontarget));
		
		scene.getStylesheets().add(Main.class.getResource("login.css").toExternalForm());

		stage_login.setScene(scene);
		stage_login.show();
	}
}