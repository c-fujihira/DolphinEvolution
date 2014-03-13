/*
 * Copyright (C) 2013 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package open.dolphin.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FX Dialog
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class FXDialog {

    private static final int W = 300;
    private static final int H = 120;

    private static final int BUTTON_W = 80;

    public static void show(String title, String message, final Stage owner) {

        final Stage stage = new Stage();
        stage.setResizable(false);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setTitle(title);
        Group root = new Group();
        StackPane stack = new StackPane();
        Scene scene = new Scene(root, W, H, Color.WHITE);

        Text msg = new Text(message);
        msg.setFill(Color.BLACK);

        stack.getChildren().addAll(msg);
        stack.setPrefSize(W, H);
        stack.setAlignment(Pos.CENTER);

        Button button = new Button();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.hide();
            }
        });

        button.setPrefWidth(BUTTON_W);
        button.setLayoutX(W / 2 - BUTTON_W / 2);
        button.setLayoutY(80);
        button.setText("OK");

        root.getChildren().addAll(stack, button);
        stage.setScene(scene);
        stage.show();
        stage.showAndWait();
    }
}
