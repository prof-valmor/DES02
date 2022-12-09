package model.plant;

import java.util.Vector;

/**
 * Represents a valve on the system which gets updated on a time base.
 */
public class Valve implements OnTimeTick {
    private ValveState state;
    private Vector<ValveListener> listenersList;
    /**
     * flow l/time
     */
    private double flow;

    public Valve(double flow) {
        state = ValveState.CLOSED;
        this.flow = flow;
        listenersList = new Vector<>(1);
    }

    public void setState(ValveState state) {
        this.state = state;
    }

    public ValveState getState() {
        return state;
    }

    public void addListener(ValveListener listener) {
        if(!listenersList.contains(listener)) listenersList.add(listener);
    }

    @Override
    public void onTimePass(int secondsSinceLastCall) {
        if(state == ValveState.OPEN || state == ValveState.STUCK_OPEN)
            for(ValveListener l : listenersList) l.onValveUpdate(this.flow * secondsSinceLastCall);
    }

    public void closeIt() {
        setState(ValveState.CLOSED);
    }

    public void openIt() {
        setState(ValveState.OPEN);
    }
}
