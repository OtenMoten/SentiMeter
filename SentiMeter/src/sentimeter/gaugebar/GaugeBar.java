package sentimeter.gaugebar;

import java.util.UUID;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Control;

public class GaugeBar extends Control {

    // 'UUID.randomUUID().toString()' needed, else error-message.
    // Error will be:
    // "Caused by: java.lang.IllegalArgumentException: 
    // EventType "javafx.event.EventType@5c9f5f72" with
    // parent "EVENT" already exists [...]".
    // These both constants will be need for the 'this.fireEvent(...)'.
    public static final EventType<Event> EVENT_TYPE_CHANGE_VALUE
            = new EventType<>(UUID.randomUUID().toString());
    public static final EventType<Event> EVENT_TYPE_CHANGE_MAX_VALUE
            = new EventType<>(UUID.randomUUID().toString());

    protected int maxValue = 100;
    protected int currentValue = this.maxValue;

    public GaugeBar() {
        setSkin(new GaugeBarSkin(this));
    }

    // Setter
    public void setMaxValue(int newMaxValue) {
        if (newMaxValue < this.currentValue) {
            System.out.println("Max value must be bigger than the current value!");
            throw new IllegalArgumentException("Max value must be bigger than the current value!");
        } else {
            this.maxValue = newMaxValue;
            // '.fireEvent' will notify all nodes in the hierarchy.
            this.fireEvent(new Event(newMaxValue, this, EVENT_TYPE_CHANGE_MAX_VALUE));
        }

    }
    
    // Setter
    public void setValue(int newValue) {
        if (this.maxValue < newValue && newValue < 0) {
            System.out.println("Value must be smaller than max value!");
            throw new IllegalArgumentException("Value must be smaller than max value!");
        } else {
            this.currentValue = newValue;
             // '.fireEvent' will notify all nodes in the hierarchy.
            this.fireEvent(new Event(newValue, this, EVENT_TYPE_CHANGE_VALUE));
        }
    }

    // Getter
    public int getMaxValue() {
        return this.maxValue;
    }

    // Getter
    public int getValue() {
        return this.currentValue;
    }

}