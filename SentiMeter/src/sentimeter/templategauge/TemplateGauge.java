package sentimeter.templategauge;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.InnerShadowBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 *
 * @author Gerrit Grunwald han.solo.gg@gmail.com
 */
public class TemplateGauge extends Region {

    private static final double DEFAULT_WIDTH = 400;
    private static final double DEFAULT_HEIGHT = 400;
    private static final double MINIMUM_WIDTH = 5;
    private static final double MINIMUM_HEIGHT = 5;
    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 1024;
    private static final double ANGLE_RANGE = 300;
    private static final double ROTATION_OFFSET = 150;
    private static final double TIME_TO_VALUE = 2500;
    private static final double FRACTION = 2;
    private static double ASPECT_RATIO;
    private double size;
    private double width;
    private double height;
    private final DoubleProperty value;
    private final DoubleProperty minValue;
    private final DoubleProperty maxValue;
    private final DoubleProperty minMeasuredValue;
    private final DoubleProperty maxMeasuredValue;
    private final DoubleProperty threshold;
    private final BooleanProperty thresholdExceeded;
    private final BooleanProperty thresholdBehaviorInverted;
    private final BooleanProperty animated;
    private final BooleanProperty backgroundVisible;
    private final Timeline timeline;
    private double angleStep;
    private final Pane pane;
    private Region frame;
    private Region background;
    private Region needle;
    private Group pointerGroup;
    private final Rotate needleRotate;
    private Region knob;
    private Text unitText;
    private final StringProperty unitString;
    private Font unitFont;
    private Text valueText;
    private Font valueFont;
    private final List<Text> tickLabels;
    private Font tickLabelFont;
    private Group tickLabelGroup;
    private Canvas alertIndicator;
    private InnerShadow gaugeKnobInnerShadow0;
    private InnerShadow gaugeKnobInnerShadow1;
    private DropShadow gaugeKnobDropShadow;
    private DropShadow pointerShadow;
    private DropShadow textDropShadow;
    private final ObservableList<Section> obsListSection;
    private Group sectionGroup;
    private ChangeListener<Number> sizeListener;

    // Constructor
    public TemplateGauge() {
        
        this.getStylesheets().add(getClass().getResource("StyleSheetTemplateGauge.css").toExternalForm());
        // Section in file 'StyleSheetTemplateGauge.css'.
        this.getStyleClass().add("templategauge");
        
        TemplateGauge.ASPECT_RATIO = 400 / 400;
        this.pane = new Pane();
        // Symbol in the mid of the gauge.
        this.unitString = new SimpleStringProperty("%");
        // Initiate the obersever-list, that is a type of the class 'Section'.
        this.obsListSection = FXCollections.observableArrayList();
        // Define a bunch of values.
        this.value = new SimpleDoubleProperty(0);
        this.minValue = new SimpleDoubleProperty(0);
        this.maxValue = new SimpleDoubleProperty(100);
        this.minMeasuredValue = new SimpleDoubleProperty(100);
        this.maxMeasuredValue = new SimpleDoubleProperty(0);
        // Threshold.
        this.threshold = new SimpleDoubleProperty(50);
        this.thresholdExceeded = new SimpleBooleanProperty(false);
        this.thresholdBehaviorInverted = new SimpleBooleanProperty(false);
        // GUI stuff.
        this.angleStep = TemplateGauge.ANGLE_RANGE / (this.getMaxValue() - this.getMinValue());
        this.animated = new SimpleBooleanProperty(true);
        this.backgroundVisible = new SimpleBooleanProperty(true);
        this.timeline = new Timeline();
        this.needleRotate = new Rotate(- TemplateGauge.ROTATION_OFFSET);
        this.tickLabels = new ArrayList<>();
        
        // When a 'TemplateGauge' will be instanced, 
        // then the following functions are executed.
        init();
        initGraphics();
        registerListeners();
        
    }

    // Initialization
    private void init() {
        
        if (Double.compare(this.getPrefWidth(), 0.0) <= 0
                || Double.compare(this.getPrefHeight(), 0.0) <= 0
                || Double.compare(this.getWidth(), 0.0) <= 0
                || Double.compare(this.getHeight(), 0.0) <= 0) {
            if (this.getPrefWidth() > 0 && this.getPrefHeight() > 0) {
                this.setPrefSize(this.getPrefWidth(), this.getPrefHeight());
            } else {
                // 'DEFAULT_WIDTH' and 'DEFAULT_HEIGHT' are defined at the beginning of the class.
                this.setPrefSize(TemplateGauge.DEFAULT_WIDTH, TemplateGauge.DEFAULT_HEIGHT);
            }
        } else {/**/}
        
        // Set default-values if the minimal width or height of this region (the extended class) are
        // less-equal '0.0'.
        if (Double.compare(this.getMinWidth(), 0.0) <= 0 || Double.compare(this.getMinHeight(), 0.0) <= 0) {
            // 'MINIMUM_WIDTH' and 'MINIMUM_HEIGHT' are defined at the beginning of the class.
            this.setMinSize(TemplateGauge.MINIMUM_WIDTH, TemplateGauge.MINIMUM_HEIGHT);
        }
        
        // Set default-values if the maximal width or height of this region (the extended class) are
        // less-equal '0.0'.
        if (Double.compare(this.getMaxWidth(), 0.0) <= 0 || Double.compare(this.getMaxHeight(), 0.0) <= 0) {
            // 'MAXIMUM_WIDTH' and 'MAXIMUM_HEIGHT' are defined at the beginning of the class.
            this.setMaxSize(TemplateGauge.MAXIMUM_WIDTH, TemplateGauge.MAXIMUM_HEIGHT);
        }
        
        // Calculate the 'ASPECT_RATIO' if the prefered width or height of this region (the extended class) are
        // different to 'DEFAULT_WIDTH or 'DEFAULT_HEIGHT' which are defined the beginning of the class.
        if (this.getPrefWidth() != TemplateGauge.DEFAULT_WIDTH || this.getPrefHeight() != TemplateGauge.DEFAULT_HEIGHT) {
            TemplateGauge.ASPECT_RATIO = this.getPrefHeight() / this.getPrefWidth();
        }
        
        this.sizeListener = (ObservableValue<? extends Number> objectValue, Number oldValue, Number newValue) -> {
            // Resize the gauge if the listener noticed a change of the GUI's attributes.
            this.resize();
            // Calculate a new value for 'ASPECT_RATIO' after the gauge was resized.
            TemplateGauge.ASPECT_RATIO = this.getPrefHeight() / this.getPrefWidth();
        };
        
    }

    private void initGraphics() {
        
        this.frame = new Region();
        this.frame.getStyleClass().setAll("frame");

        this.background = new Region();
        this.background.getStyleClass().setAll("background");

        this.sectionGroup = new Group();
        getSections().forEach((section) -> {
            this.sectionGroup.getChildren().add(section.getArea());
        });

        this.textDropShadow = DropShadowBuilder.create()
                .radius(TemplateGauge.DEFAULT_WIDTH * 0.005)
                .offsetY(TemplateGauge.DEFAULT_WIDTH * 0.0025)
                .blurType(BlurType.GAUSSIAN)
                .color(Color.rgb(0, 0, 0, 0.65))
                .build();

        this.tickLabelFont = Font.font("Verdana", FontWeight.BOLD, TemplateGauge.DEFAULT_WIDTH * 0.04);
        for (double i = this.getMinValue(); Double.compare(i, this.getMaxValue()) <= 0; i += TemplateGauge.FRACTION) {
            Text tickLabel = TextBuilder.create()
                    .font(this.tickLabelFont)
                    .text(String.format("%.0f", i))
                    .effect(this.textDropShadow)
                    .styleClass("tick-label")
                    .build();
            this.tickLabels.add(tickLabel);
        }
        this.tickLabelGroup = GroupBuilder.create()
                .children(this.tickLabels)
                .build();

        this.needle = new Region();
        this.needle.setEffect(new InnerShadow());
        this.needle.getStyleClass().setAll("needle");
        this.needle.getTransforms().setAll(this.needleRotate);

        this.pointerGroup = new Group(this.needle);
        this.pointerGroup.setEffect(this.pointerShadow);
        this.pointerShadow = DropShadowBuilder.create()
                .radius(0.05 * DEFAULT_WIDTH)
                .offsetY(0.02 * DEFAULT_WIDTH)
                .blurType(BlurType.GAUSSIAN)
                .color(Color.rgb(0, 0, 0, 0.65))
                .build();

        this.pointerGroup = new Group(this.needle);
        this.pointerGroup.setEffect(this.pointerShadow);

        this.knob = new Region();
        this.knob.getStyleClass().setAll("knob");

        this.gaugeKnobInnerShadow0 = InnerShadowBuilder.create()
                .offsetY(2.0)
                .radius(0.005 * DEFAULT_WIDTH)
                .color(Color.rgb(255, 255, 255, 0.45))
                .blurType(BlurType.GAUSSIAN)
                .build();
        this.gaugeKnobInnerShadow1 = InnerShadowBuilder.create()
                .offsetY(-2.0)
                .radius(0.005 * DEFAULT_WIDTH)
                .color(Color.rgb(0, 0, 0, 0.65))
                .blurType(BlurType.GAUSSIAN)
                .input(this.gaugeKnobInnerShadow0)
                .build();
        this.gaugeKnobDropShadow = DropShadowBuilder.create()
                .offsetY(0.02 * DEFAULT_WIDTH)
                .radius(0.05 * DEFAULT_WIDTH)
                .color(Color.rgb(0, 0, 0, 0.65))
                .blurType(BlurType.GAUSSIAN)
                .input(this.gaugeKnobInnerShadow1)
                .build();
        this.knob.setEffect(this.gaugeKnobDropShadow);

        this.unitFont = Font.font("Verdana", FontWeight.NORMAL, DEFAULT_WIDTH * 0.16);
        this.unitText = TextBuilder.create()
                .textOrigin(VPos.CENTER)
                .textAlignment(TextAlignment.CENTER)
                .font(this.unitFont)
                .effect(this.textDropShadow)
                .styleClass("unit")
                .build();
        this.unitText.textProperty().bind(this.unitString);

        this.valueFont = Font.font("Verdana", FontWeight.NORMAL, DEFAULT_WIDTH * 0.07);
        this.valueText = TextBuilder.create()
                .text("0.00")
                .textOrigin(VPos.CENTER)
                .textAlignment(TextAlignment.CENTER)
                .effect(this.textDropShadow)
                .font(this.valueFont)
                .styleClass("value")
                .build();

        this.alertIndicator = new Canvas();
        this.alertIndicator.visibleProperty().bind(this.thresholdExceeded);
        this.drawAlertIndicator(0.124 * DEFAULT_WIDTH, 0.108 * DEFAULT_HEIGHT, Color.RED);

        this.pane.getChildren().setAll(
                this.frame,
                this.background,
                this.sectionGroup,
                this.tickLabelGroup,
                this.valueText,
                this.alertIndicator,
                this.pointerGroup,
                this.knob,
                this.unitText
        );

        this.getChildren().setAll(this.pane);
        this.resize();
        
    }
    
    private void registerListeners() {
        
        this.widthProperty().addListener(this.sizeListener);
        this.heightProperty().addListener(this.sizeListener);
        this.prefWidthProperty().addListener(this.sizeListener);
        this.prefHeightProperty().addListener(this.sizeListener);

        this.backgroundVisible.addListener((Observable o) -> {
            this.frame.setVisible(this.isBackgroundVisible());
            this.background.setVisible(this.isBackgroundVisible());
        });

        valueProperty().addListener((Observable observableObject) -> {
            this.valueText.setText(String.format("%.2f", this.getValue()));
            this.rotateNeedle();
            if (this.getValue() < this.getMinMeasuredValue()) {
                this.setMinMeasuredValue(this.getValue());
            } else if (this.getValue() > this.getMaxMeasuredValue()) {
                this.setMaxMeasuredValue(this.getValue());
            }
            if (isThresholdBehaviorInverted() && this.getValue() < this.getThreshold()) {
                this.setThresholdExceeded(true);
            } else if (!isThresholdBehaviorInverted() && this.getValue() > this.getThreshold()) {
                this.setThresholdExceeded(true);
            } else {
                this.setThresholdExceeded(false);
            }
        });
        
    }

    // Public methods
    public final double getValue() {
        return this.value.get();
    }

    public final void setValue(final double VALUE) {
        this.value.set(this.clamp(this.getMinValue(), this.getMaxValue(), VALUE));
    }

    public final DoubleProperty valueProperty() {
        return this.value;
    }

    public final double getMinValue() {
        return this.minValue.get();
    }

    public final void setMinValue(final double MIN_VALUE) {
        this.minValue.set(this.clamp(Double.NEGATIVE_INFINITY, this.getMaxValue() - 1, MIN_VALUE));
        this.recalculate();
        this.updateTickLabels();
    }

    public final DoubleProperty minValueProperty() {
        return this.minValue;
    }

    public final double getMaxValue() {
        return this.maxValue.get();
    }

    public final void setMaxValue(final double MAX_VALUE) {
        this.maxValue.set(this.clamp(this.getMinValue() + 1, Double.POSITIVE_INFINITY, MAX_VALUE));
        this.recalculate();
        this.updateTickLabels();
    }

    public final DoubleProperty maxValueProperty() {
        return this.maxValue;
    }

    public final double getMinMeasuredValue() {
        return this.minMeasuredValue.get();
    }

    public final void setMinMeasuredValue(final double MIN_MEASURED_VALUE) {
        this.minMeasuredValue.set(MIN_MEASURED_VALUE);
    }

    public final DoubleProperty minMeasuredValueProperty() {
        return this.minMeasuredValue;
    }

    public final double getMaxMeasuredValue() {
        return this.maxMeasuredValue.get();
    }

    public final void setMaxMeasuredValue(final double MAX_MEASURED_VALUE) {
        this.maxMeasuredValue.set(MAX_MEASURED_VALUE);
    }

    public final DoubleProperty maxMeasuredValueProperty() {
        return this.maxMeasuredValue;
    }

    public final void resetMinMeasuredValue() {
        this.setMinMeasuredValue(this.getValue());
    }

    public final void resetMaxMeasuredValue() {
        this.setMaxMeasuredValue(this.getValue());
    }

    public final void resetMinMaxMeasuredValue() {
        this.setMinMeasuredValue(this.getValue());
        this.setMaxMeasuredValue(this.getValue());
    }

    public final double getThreshold() {
        return this.threshold.get();
    }

    public final void setThreshold(final double THRESHOLD) {
        this.threshold.set(THRESHOLD);
    }

    public final DoubleProperty thresholdProperty() {
        return this.threshold;
    }

    public final boolean isThresholdBehaviorInverted() {
        return this.thresholdBehaviorInverted.get();
    }

    public final void setThresholdBehaviorInverted(final boolean THRESHOLD_BEHAVIOR_INVERTED) {
        this.thresholdBehaviorInverted.set(THRESHOLD_BEHAVIOR_INVERTED);
    }

    public final BooleanProperty thresholdBehaviorInvertedProperty() {
        return this.thresholdBehaviorInverted;
    }

    public final boolean isThresholdExceeded() {
        return this.thresholdExceeded.get();
    }

    public final void setThresholdExceeded(final boolean THRESHOLD_EXCEEDED) {
        this.thresholdExceeded.set(THRESHOLD_EXCEEDED);
    }

    public final BooleanProperty thresholdExceededProperty() {
        return this.thresholdExceeded;
    }

    public final boolean isAnimated() {
        return this.animated.get();
    }

    public final void setAnimated(final boolean ANIMATED) {
        this.animated.set(ANIMATED);
    }

    public final BooleanProperty animatedProperty() {
        return this.animated;
    }

    public final boolean isBackgroundVisible() {
        return this.backgroundVisible.get();
    }

    public final void setBackgroundVisible(final boolean BACKGROUND_VISIBLE) {
        this.backgroundVisible.set(BACKGROUND_VISIBLE);
    }

    public final BooleanProperty backgroundVisibleProperty() {
        return this.backgroundVisible;
    }

    public final String getUnitString() {
        return this.unitString.get();
    }

    public final void setUnitString(final String UNIT_STRING) {
        this.unitString.set(UNIT_STRING);
    }

    public final StringProperty unitStringProperty() {
        return this.unitString;
    }

    public final ObservableList<Section> getSections() {
        return this.obsListSection;
    }

    public final void setSections(final Section... SECTION_ARRAY) {
        this.obsListSection.setAll(SECTION_ARRAY);
        this.addSections();
    }

    public final void setSections(final List<Section> SECTIONS) {
        this.obsListSection.setAll(SECTIONS);
        this.addSections();
    }

    @Override
    protected double computePrefWidth(final double PREF_HEIGHT) {
        double prefHeight = TemplateGauge.DEFAULT_HEIGHT;
        if (PREF_HEIGHT != -1) {
            prefHeight = Math.max(0, PREF_HEIGHT - this.getInsets().getTop() - this.getInsets().getBottom());
        }
        return super.computePrefWidth(prefHeight);
    }

    @Override
    protected double computePrefHeight(final double PREF_WIDTH) {
        double prefWidth = DEFAULT_WIDTH;
        if (PREF_WIDTH != -1) {
            prefWidth = Math.max(0, PREF_WIDTH - this.getInsets().getLeft() - this.getInsets().getRight());
        }
        return super.computePrefWidth(prefWidth);
    }

    @Override
    protected double computeMinWidth(final double MIN_HEIGHT) {
        return super.computeMinWidth(Math.max(MINIMUM_HEIGHT, MIN_HEIGHT - this.getInsets().getTop() - this.getInsets().getBottom()));
    }

    @Override
    protected double computeMinHeight(final double MIN_WIDTH) {
        return super.computeMinHeight(Math.max(MINIMUM_WIDTH, MIN_WIDTH - this.getInsets().getLeft() - this.getInsets().getRight()));
    }

    @Override
    protected double computeMaxWidth(final double MAX_HEIGHT) {
        return super.computeMaxWidth(Math.min(MAXIMUM_HEIGHT, MAX_HEIGHT - this.getInsets().getTop() - this.getInsets().getBottom()));
    }

    @Override
    protected double computeMaxHeight(final double MAX_WIDTH) {
        return super.computeMaxHeight(Math.min(MAXIMUM_WIDTH, MAX_WIDTH - this.getInsets().getLeft() - this.getInsets().getRight()));
    }

    // Private methods
    private void rotateNeedle() {
        
        this.valueText.setX((this.width - this.valueText.getLayoutBounds().getWidth()) * 0.5);
        this.valueText.setY(this.size * 0.85);

        double targetAngle = (this.getValue() - this.getMinValue()) * this.angleStep - TemplateGauge.ROTATION_OFFSET;
        if (this.isAnimated()) {
            this.needle.setCache(true);
            this.needle.setCacheHint(CacheHint.ROTATE);
            this.timeline.stop();
            final KeyValue KEY_VALUE = new KeyValue(this.needleRotate.angleProperty(), targetAngle, Interpolator.SPLINE(0.5, 0.4, 0.4, 1.0));
            final KeyFrame KEY_FRAME = new KeyFrame(Duration.millis(TemplateGauge.TIME_TO_VALUE), KEY_VALUE);
            this.timeline.getKeyFrames().setAll(KEY_FRAME);
            this.timeline.getKeyFrames().add(KEY_FRAME);
            this.timeline.play();
            this.timeline.setOnFinished((ActionEvent event) -> {
                needle.setCache(false);
            });
        } else {
            this.needleRotate.setAngle(targetAngle);
        }
        
    }

    private void recalculate() {
        this.angleStep = TemplateGauge.ANGLE_RANGE / (this.getMaxValue() - this.getMinValue());
        this.setThreshold(this.clamp(this.getMinValue(), this.getMaxValue(), this.getThreshold()));
    }

    private void addSections() {
        this.sectionGroup.getChildren().clear();
        this.getSections().forEach((section) -> {
            this.sectionGroup.getChildren().add(section.getArea());
        });
        this.updateSections();
    }

    private void updateSections() {
        
        final double OUTER_RADIUS = this.size * 0.41;
        final double CENTER_X = this.width * 0.5;
        final double CENTER_Y = this.height * 0.5;

        this.getSections().stream().map((section) -> {
            final double SECTION_START = this.clamp(this.getMinValue(), this.getMaxValue(), section.getStart());
            final double SECTION_STOP = this.clamp(this.getMinValue(), this.getMaxValue(), section.getStop());
            final double ANGLE_START = TemplateGauge.ROTATION_OFFSET - (SECTION_START * this.angleStep) + (this.getMinValue() * this.angleStep) + 90;
            final double ANGLE_EXTEND = -(SECTION_STOP - SECTION_START) * this.angleStep;
            section.getArea().setType(ArcType.ROUND);
            section.getArea().setCenterX(CENTER_X);
            section.getArea().setCenterY(CENTER_Y);
            section.getArea().setRadiusX(OUTER_RADIUS);
            section.getArea().setRadiusY(OUTER_RADIUS);
            section.getArea().setStartAngle(ANGLE_START);
            section.getArea().setLength(ANGLE_EXTEND);
            return section;
        }).forEachOrdered((section) -> {
            section.getArea().setFill(section.getColor());
        });
        
    }

    private void updateTickLabels() {
        
        this.tickLabelGroup.getChildren().clear();
        this.tickLabels.clear();
        
        for (double i = this.getMinValue(); Double.compare(i, this.getMaxValue()) <= 0; i += TemplateGauge.FRACTION) {
            Text tickLabel = TextBuilder.create()
                    .font(this.tickLabelFont)
                    .text(String.format("%.0f", i))
                    .effect(this.textDropShadow)
                    .styleClass("tick-label")
                    .build();
            this.tickLabels.add(tickLabel);
        }
        
        this.tickLabelGroup.getChildren().setAll(this.tickLabels);
        this.resize();
        
    }

    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) {
            return MIN;
        }
        if (VALUE > MAX) {
            return MAX;
        }
        return VALUE;
    }

    public final void drawAlertIndicator(final double WIDTH, final double HEIGHT, final Color COLOR) {
        
        this.alertIndicator.setWidth(WIDTH);
        this.alertIndicator.setHeight(HEIGHT);
        final GraphicsContext GCTX = this.alertIndicator.getGraphicsContext2D();
        GCTX.clearRect(0, 0, WIDTH, HEIGHT);

        // Alert.
        GCTX.save();
        GCTX.beginPath();
        GCTX.moveTo(0.45161290322580644 * WIDTH, 0.8148148148148148 * HEIGHT);
        GCTX.bezierCurveTo(0.45161290322580644 * WIDTH, 0.7777777777777778 * HEIGHT, 0.4838709677419355 * WIDTH, 0.7407407407407407 * HEIGHT, 0.5161290322580645 * WIDTH, 0.7407407407407407 * HEIGHT);
        GCTX.bezierCurveTo(0.5161290322580645 * WIDTH, 0.7407407407407407 * HEIGHT, 0.5483870967741935 * WIDTH, 0.7777777777777778 * HEIGHT, 0.5483870967741935 * WIDTH, 0.8148148148148148 * HEIGHT);
        GCTX.bezierCurveTo(0.5483870967741935 * WIDTH, 0.8148148148148148 * HEIGHT, 0.5161290322580645 * WIDTH, 0.8518518518518519 * HEIGHT, 0.5161290322580645 * WIDTH, 0.8518518518518519 * HEIGHT);
        GCTX.bezierCurveTo(0.4838709677419355 * WIDTH, 0.8518518518518519 * HEIGHT, 0.45161290322580644 * WIDTH, 0.8148148148148148 * HEIGHT, 0.45161290322580644 * WIDTH, 0.8148148148148148 * HEIGHT);
        GCTX.closePath();
        GCTX.moveTo(0.45161290322580644 * WIDTH, 0.37037037037037035 * HEIGHT);
        GCTX.bezierCurveTo(0.45161290322580644 * WIDTH, 0.3333333333333333 * HEIGHT, 0.4838709677419355 * WIDTH, 0.3333333333333333 * HEIGHT, 0.5161290322580645 * WIDTH, 0.3333333333333333 * HEIGHT);
        GCTX.bezierCurveTo(0.5161290322580645 * WIDTH, 0.3333333333333333 * HEIGHT, 0.5483870967741935 * WIDTH, 0.3333333333333333 * HEIGHT, 0.5483870967741935 * WIDTH, 0.37037037037037035 * HEIGHT);
        GCTX.bezierCurveTo(0.5483870967741935 * WIDTH, 0.37037037037037035 * HEIGHT, 0.5483870967741935 * WIDTH, 0.6296296296296297 * HEIGHT, 0.5483870967741935 * WIDTH, 0.6296296296296297 * HEIGHT);
        GCTX.bezierCurveTo(0.5483870967741935 * WIDTH, 0.6666666666666666 * HEIGHT, 0.5161290322580645 * WIDTH, 0.7037037037037037 * HEIGHT, 0.5161290322580645 * WIDTH, 0.7037037037037037 * HEIGHT);
        GCTX.bezierCurveTo(0.4838709677419355 * WIDTH, 0.7037037037037037 * HEIGHT, 0.45161290322580644 * WIDTH, 0.6666666666666666 * HEIGHT, 0.45161290322580644 * WIDTH, 0.6296296296296297 * HEIGHT);
        GCTX.bezierCurveTo(0.45161290322580644 * WIDTH, 0.6296296296296297 * HEIGHT, 0.45161290322580644 * WIDTH, 0.37037037037037035 * HEIGHT, 0.45161290322580644 * WIDTH, 0.37037037037037035 * HEIGHT);
        GCTX.closePath();
        GCTX.moveTo(0.3225806451612903 * WIDTH, 0.9629629629629629 * HEIGHT);
        GCTX.lineTo(0.6451612903225806 * WIDTH, 0.9629629629629629 * HEIGHT);
        GCTX.bezierCurveTo(0.6451612903225806 * WIDTH, 0.9629629629629629 * HEIGHT, 0.8387096774193549 * WIDTH, 0.9629629629629629 * HEIGHT, 0.8387096774193549 * WIDTH, 0.9629629629629629 * HEIGHT);
        GCTX.bezierCurveTo(0.9354838709677419 * WIDTH, 0.9629629629629629 * HEIGHT, 0.967741935483871 * WIDTH, 0.8888888888888888 * HEIGHT, 0.9032258064516129 * WIDTH, 0.8148148148148148 * HEIGHT);
        GCTX.bezierCurveTo(0.9032258064516129 * WIDTH, 0.8148148148148148 * HEIGHT, 0.5806451612903226 * WIDTH, 0.1111111111111111 * HEIGHT, 0.5806451612903226 * WIDTH, 0.1111111111111111 * HEIGHT);
        GCTX.bezierCurveTo(0.5161290322580645 * WIDTH, 0.037037037037037035 * HEIGHT, 0.45161290322580644 * WIDTH, 0.037037037037037035 * HEIGHT, 0.41935483870967744 * WIDTH, 0.1111111111111111 * HEIGHT);
        GCTX.bezierCurveTo(0.41935483870967744 * WIDTH, 0.1111111111111111 * HEIGHT, 0.06451612903225806 * WIDTH, 0.8148148148148148 * HEIGHT, 0.06451612903225806 * WIDTH, 0.8148148148148148 * HEIGHT);
        GCTX.bezierCurveTo(0.03225806451612903 * WIDTH, 0.8888888888888888 * HEIGHT, 0.06451612903225806 * WIDTH, 0.9629629629629629 * HEIGHT, 0.16129032258064516 * WIDTH, 0.9629629629629629 * HEIGHT);
        GCTX.bezierCurveTo(0.16129032258064516 * WIDTH, 0.9629629629629629 * HEIGHT, 0.3225806451612903 * WIDTH, 0.9629629629629629 * HEIGHT, 0.3225806451612903 * WIDTH, 0.9629629629629629 * HEIGHT);
        GCTX.closePath();
        GCTX.setFill(COLOR);
        GCTX.fill();
        GCTX.restore();
        
    }

    private void resize() {
        
        this.size = this.getWidth() < this.getHeight() ? this.getWidth() : this.getHeight();
        this.width = this.getWidth();
        this.height = this.getHeight();

        if (TemplateGauge.ASPECT_RATIO * this.width > this.height) {
            this.width = 1 / (TemplateGauge.ASPECT_RATIO / this.height);
        } else if (1 / (TemplateGauge.ASPECT_RATIO / this.height) > this.width) {
            this.height = TemplateGauge.ASPECT_RATIO * this.width;
        }

        this.frame.setPrefSize(1.0 * this.width, 1.0 * this.height);
        this.frame.setTranslateX(0.0 * this.width);
        this.frame.setTranslateY(0.0 * this.height);

        this.background.setPrefSize(0.955 * this.width, 0.955 * this.height);
        this.background.setTranslateX(0.0225 * this.width);
        this.background.setTranslateY(0.0225 * this.height);

        this.updateSections();

        this.textDropShadow.setRadius(this.size * 0.005);
        this.textDropShadow.setOffsetY(this.size * 0.0025);

        this.tickLabelFont = Font.font("Verdana", FontWeight.BOLD, this.size * 0.04);
        int tickLabelCounter = 0;
        for (double angle = -30; Double.compare(angle, -ANGLE_RANGE - 30) >= 0; angle -= (FRACTION * this.angleStep)) {
            double x = 0.31 * this.size * Math.sin(Math.toRadians(angle));
            double y = 0.31 * this.size * Math.cos(Math.toRadians(angle));
            this.tickLabels.get(tickLabelCounter).setFont(this.tickLabelFont);
            this.tickLabels.get(tickLabelCounter).setX(this.size * 0.5 + x - this.tickLabels.get(tickLabelCounter).getLayoutBounds().getWidth() * 0.5);
            this.tickLabels.get(tickLabelCounter).setY(this.size * 0.5 + y);
            this.tickLabels.get(tickLabelCounter).setTextOrigin(VPos.CENTER);
            this.tickLabels.get(tickLabelCounter).setTextAlignment(TextAlignment.CENTER);
            tickLabelCounter++;
        }

        this.pointerShadow.setRadius(0.05 * this.size);
        this.pointerShadow.setOffsetY(0.02 * this.size);

        this.needle.setPrefSize(0.3234653091430664 * this.width, 0.4225 * this.height);
        this.needle.setTranslateX(0.33826732635498047 * this.width);
        this.needle.setTranslateY(0.07625 * this.height);
        this.needleRotate.setPivotX((this.needle.getPrefWidth()) * 0.5);
        this.needleRotate.setPivotY(this.needle.getPrefHeight());

        this.knob.setPrefSize(0.295 * this.width, 0.295 * this.height);
        this.knob.setTranslateX(0.3525 * this.width);
        this.knob.setTranslateY(0.345 * this.height);
        this.gaugeKnobInnerShadow0.setRadius(0.005 * this.size);
        this.gaugeKnobInnerShadow1.setRadius(0.005 * this.size);
        this.gaugeKnobDropShadow.setRadius(0.05 * this.size);
        this.gaugeKnobDropShadow.setOffsetY(0.02 * this.size);

        this.unitFont = Font.font("Verdana", FontWeight.NORMAL, this.size * 0.16);
        this.unitText.setFont(unitFont);
        this.unitText.setX((this.width - this.unitText.getLayoutBounds().getWidth()) * 0.5);
        this.unitText.setY((this.height - this.unitText.getLayoutBounds().getHeight()) * 0.5 + this.unitText.getLayoutBounds().getHeight() * 0.5);

        this.valueFont = Font.font("Verdana", FontWeight.NORMAL, this.size * 0.07);
        this.valueText.setFont(this.valueFont);
        this.valueText.setX((this.width - this.valueText.getLayoutBounds().getWidth()) * 0.5);
        this.valueText.setY(this.size * 0.85);

        this.drawAlertIndicator(0.124 * this.size, 0.108 * this.size, Color.RED);
        this.alertIndicator.setTranslateX((this.width - this.alertIndicator.getLayoutBounds().getWidth()) * 0.5);
        this.alertIndicator.setTranslateY(this.height * 0.68);

    }

}