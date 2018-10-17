package sentimeter.tgauge;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;

public class Section {

    private DoubleProperty start;
    private DoubleProperty stop;
    private ObjectProperty<Color> color;
    private ObjectProperty<Arc> area;

    public Section(final double START, final double STOP, final Color COLOR) {
        this.start = new SimpleDoubleProperty(START);
        this.stop = new SimpleDoubleProperty(STOP);
        this.color = new SimpleObjectProperty<>(COLOR);
        this.area = new SimpleObjectProperty<>(new Arc());
        this.validate();
    }

    public double getStart() {
        return this.start.get();
    }

    public void setStart(final double START) {
        this.start.set(START);
        validate();
    }

    public DoubleProperty getStartProperty() {
        return this.start;
    }

    public double getStop() {
        return this.stop.get();
    }

    public void setStop(final double STOP) {
        this.stop.set(STOP);
        validate();
    }

    public DoubleProperty getStopProperty() {
        return this.stop;
    }

    public Paint getColor() {
        return this.color.get();
    }

    public void setFill(final Color COLOR) {
        this.color.set(COLOR);
    }

    public ObjectProperty<Color> getColorProperty() {
        return this.color;
    }

    public Arc getArea() {
        return this.area.get();
    }

    public void setArea(final Arc AREA) {
        this.area.set(AREA);
    }

    public ObjectProperty<Arc> getAreaProperty() {
        return this.area;
    }
    
    public boolean contains(final double VALUE) {
        return ((Double.compare(VALUE, this.start.get()) >= 0
                && Double.compare(VALUE, this.stop.get()) <= 0));
    }
    

    // Start must be a less value than stop and vice versa.
    private void validate() {
        
        if (this.getStart() > this.getStop()) {
            // 'Normalize' if this is the case.
            this.setStart(this.getStop() - 1);
        }
        
        if (this.getStop() < this.getStart()) {
            // 'Normalize' if this is the case.
            this.setStop(this.getStart() + 1);
        }
        
    }
    
}