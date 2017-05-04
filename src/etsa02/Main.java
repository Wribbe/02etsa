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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String args[]) 
	{
		launch(args);
	}
	
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("JavaFX Welcome");
		
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
		
		signIn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				actiontarget.setText("Sign in button pressed.");
			}
		});

		passwordBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent key){
				if (key.getCode() == KeyCode.ENTER) {
					signIn.fire();
				}
			}
		});
		
		scene.getStylesheets().add(Main.class.getResource("login.css").toExternalForm());

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}