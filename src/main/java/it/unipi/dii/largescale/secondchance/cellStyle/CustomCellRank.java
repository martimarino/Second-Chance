package main.java.it.unipi.dii.largescale.secondchance.cellStyle;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;


public class CustomCellRank extends HBox {

    private Label username = new Label();
    private Label total = new Label();
    private GridPane grid = new GridPane();

    public CustomCellRank(String userName, int totalScore) {
        super();

        this.username.setTextFill(Color.BLUEVIOLET);
        this.username.setText(userName);
        this.total.setText("Total: " + totalScore);

        grid.setHgap(10);
        grid.setVgap(6);
        this.username.setMaxWidth(Double.MAX_VALUE);
        this.total.setMaxWidth(Double.MAX_VALUE);

        grid.add(this.username,0,0);
        grid.add(this.total,0,1);
        this.setHgrow(grid, Priority.ALWAYS);

        this.getChildren().addAll(grid);
    }

}
