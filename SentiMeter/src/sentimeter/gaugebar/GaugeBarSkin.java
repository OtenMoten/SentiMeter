package sentimeter.gaugebar;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

public class GaugeBarSkin implements Skin<GaugeBar> {

    private static final int GAUGE_MAX_SIZE = 10;
    private static final int TICK_DEGREE = 12;
    private static final int RADIUS = 120;

    private final GaugeBar myGaugeBar;
    private Group rootGroupNode;
    private final int mySize = 50;

    public GaugeBarSkin(GaugeBar initialGaugeBar) {
        this.myGaugeBar = initialGaugeBar;
        this.hookEventHandler();
    }

    private void hookEventHandler() {
        this.myGaugeBar.addEventHandler(GaugeBar.EVENT_TYPE_CHANGE_VALUE, (Event event) -> {
            this.redraw();
        });
        this.myGaugeBar.addEventHandler(GaugeBar.EVENT_TYPE_CHANGE_MAX_VALUE, (Event event) -> {
            this.redraw();
        });
    }

    @Override
    public GaugeBar getSkinnable() {
        return this.myGaugeBar;
    }

    @Override
    public Node getNode() {
        if (this.rootGroupNode == null) {
            this.rootGroupNode = new Group();
            this.redraw();
        }
        return this.rootGroupNode;
    }

    protected void redraw() {
        // Create a list with elements that are needed for a gaugebar.
        List<Node> rootChildren = new ArrayList<>();
        // Create elements and put them directly in the list.
        rootChildren.add(createBackground());
        rootChildren.add(createGauge());
        rootChildren.add(createTicks());
        rootChildren.add(createGaugeBlend());
        rootChildren.add(createBorder());
        // Add the whole list to the myGroup-object.
        // All elements in the list are type of 'Node'.
        this.rootGroupNode.getChildren().setAll(rootChildren);
    }

    @Override
    public void dispose() {
        // nothing to do
    }

    private Node createBackground() {
        return new Circle(
                this.mySize, 
                this.mySize, 
                GaugeBarSkin.RADIUS + 1
        );
    }

    private Node createGauge() {
        Stop[] myStops = new Stop[]{
            new Stop(0, Color.AQUA),
            new Stop(1, Color.ORANGERED)
        };
        Circle myCircle = new Circle(
                this.mySize,                // CenterX
                this.mySize,                // CenterY
                GaugeBarSkin.RADIUS         // Radius
        );
        myCircle.setFill(new LinearGradient(1, 0, 0.3, 1, true, CycleMethod.NO_CYCLE, myStops));
        
        // This is the keyword from the styleSheet 'StyleSheet.css' from package 'sentimeter.gaugebar'.
        myCircle.getStyleClass().add("gauge");
        
        return myCircle;
    }

    private Node createTicks() {
        
        Path tickMarks = new Path();
        ObservableList<PathElement> pathChildren = tickMarks.getElements();
        
        for (int i = 0; i < 360; i += TICK_DEGREE) {
            pathChildren.add(new MoveTo(
                    this.mySize,    // X-coordinate
                    this.mySize));  // Y-coordinate
            pathChildren.add(new LineTo(
                    GaugeBarSkin.RADIUS * Math.cos(Math.toRadians(i)) + this.mySize,    // X-coordinate 
                    GaugeBarSkin.RADIUS * Math.sin(Math.toRadians(i)) + this.mySize));  // Y-coordinate
        }
        
        return tickMarks;
        
    }

    private Node createGaugeBlend() {
        
        Group myGroup = new Group();

        float arcBlendDegrees =  (90 
                + (1 - (float) this.myGaugeBar.currentValue / this.myGaugeBar.maxValue) 
                * 230);
        
        Arc arcBlend = new Arc(
                this.mySize,    // Center-X
                this.mySize,    // Center-Y
                RADIUS,         // Radius-X
                RADIUS,         // Radius-Y
                -90,            // Starting angle
                arcBlendDegrees // Angular extend in degrees
        );
        
        arcBlend.setType(ArcType.ROUND);
        arcBlend.setFill(Color.BLACK);

        Circle circleBlend = new Circle(
                this.mySize,                                            // Center-X
                this.mySize + 3 * GaugeBarSkin.GAUGE_MAX_SIZE / 2,      // Center-Y
                GaugeBarSkin.RADIUS - 4 * GaugeBarSkin.GAUGE_MAX_SIZE   // Radius
        );
        circleBlend.setFill(Color.BLACK);

        myGroup.getChildren().setAll(arcBlend, circleBlend);
        
        return myGroup;
        
    }

    private Node createBorder() {
        
        Circle myCircle = new Circle(
                this.mySize,            // Center-X
                this.mySize,            // Center-Y
                GaugeBarSkin.RADIUS     // Radius
        );
        
        myCircle.setFill(null);
        myCircle.setStroke(Color.WHITE);
        
        return myCircle;
        
    }

}