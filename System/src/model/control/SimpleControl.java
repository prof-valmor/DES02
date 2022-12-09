package model.control;

import model.plant.SimpleSensor;
import model.plant.SystemPlant;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleControl {
    private static SimpleControl instance;
    private Timer timer;
    private The1Task task;

    private SimpleControl() {

    }
    public void initialize(SystemPlant plant) {
        if(timer == null) {
            timer = new Timer("SimpleControl");
            task = new The1Task(plant);
            timer.scheduleAtFixedRate(task, 0, 250);
        }
    }

    public static SimpleControl getInstance() {
        if(instance == null) instance = new SimpleControl();
        return instance;
    }

    public void defineSetPoints(double low, double high) {
        task.setPointHigh = high;
        task.setPointLow  = low;
    }

    public void turnON() {
        task.isON = true;
    }
    public void turnOff() {
        task.isON = false;
    }

    public boolean isOn() {return task.isON;}

    public String getState() {
        return task.state + "";
    }

    public enum Event {
        InOpen, InClose, OutOpen, OutClose, HLow, HMid, HHigh, HMid1
    }
    public void addListener(ControlListener listener) {
        task.addListener(listener);
    }
}

class The1Task extends TimerTask {
    private SystemPlant plant;
    public boolean isON;
    private boolean isTankFull;
    private boolean isTankEmpty;
    private double currentTankVolume;
    public double setPointHigh;
    public double setPointLow;
    private ArrayList<ControlListener> listenerArrayList;

    public void addListener(ControlListener listener) {
        if(!listenerArrayList.contains(listener)) listenerArrayList.add(listener);
    }

    private void updateListeners(SimpleControl.Event event) {
        for(ControlListener l : listenerArrayList)
            l.onEvent(event);
    }

    enum STATE {
        UM, DOIS, TRES, QUATRO, CINCO, SEIS, SETE, OITO, NOVE, DEZ, ONZE, DOZE
    }
    STATE state;

    public The1Task(SystemPlant plant) {
        this.plant = plant;
        isON = false;
        state = STATE.UM;
        listenerArrayList = new ArrayList<>(1);
        //
        plant.getTheTank().setTankEmptySensor(new SimpleSensor() {
            @Override
            public void onSensorActivated() {
                isTankEmpty = true;
            }

            @Override
            public void onSensorInactivated() {
                isTankEmpty = false;
            }
        });
        plant.getTheTank().setTankFullSensor(new SimpleSensor() {
            @Override
            public void onSensorActivated() {
                isTankFull = true;
            }

            @Override
            public void onSensorInactivated() {
                isTankFull = false;
            }
        });
    }
    @Override
    public void run() {
        if(isON) {
            double currentTankVolume = plant.getTheSensor().getCurrentMeasurement();
            MEASUREMENTS level = convertMeasurement(currentTankVolume);
            // generates the event...
            // state machine.
            switch(state) {
                case UM -> {
                    if(level == MEASUREMENTS.LOW)
                        state = STATE.DOIS;
                    updateListeners(SimpleControl.Event.HLow);
                }
                case DOIS -> {
                    plant.getInputValve().openIt();
                    updateListeners(SimpleControl.Event.InOpen);
                    state = STATE.TRES;
                }
                case TRES -> {
                    if(level == MEASUREMENTS.MID)
                        state = STATE.QUATRO;
                    updateListeners(SimpleControl.Event.HMid);
                }
                case QUATRO -> {
                    if(level == MEASUREMENTS.HIGH) {
                        state = STATE.CINCO;
                        updateListeners(SimpleControl.Event.HHigh);
                    }
                    else {
                        plant.getOutputValve().openIt();
                        state = STATE.SEIS;
                        updateListeners(SimpleControl.Event.OutOpen);
                    }
                }
                case CINCO -> {
                    plant.getOutputValve().openIt();
                    state = STATE.SETE;
                    updateListeners(SimpleControl.Event.OutOpen);
                }
                case SEIS -> {
                    if(level == MEASUREMENTS.HIGH) {
                        state = STATE.SETE;
                        updateListeners(SimpleControl.Event.HHigh);
                    }
                }
                case SETE -> {
                    if(level == MEASUREMENTS.MID1) {
                        state = STATE.OITO;
                        updateListeners(SimpleControl.Event.HMid1);
                    }
                }
                case OITO -> {
                    plant.getInputValve().closeIt();
                    state = STATE.DEZ;
                    updateListeners(SimpleControl.Event.InClose);
                }
                case NOVE -> {
                }
                case DEZ -> {
                    if(level == MEASUREMENTS.LOW) {
                        state = STATE.DOZE;
                        updateListeners(SimpleControl.Event.HLow);
                    }
                }
                case ONZE -> {}
                case DOZE -> {
                    plant.getOutputValve().closeIt();
                    updateListeners(SimpleControl.Event.OutClose);
                    state = STATE.UM;

                }
            }
        }
    }
    public enum MEASUREMENTS {
        LOW, MID, HIGH, MID1
    }
    boolean previousWasHigh = false;

    private MEASUREMENTS convertMeasurement(double currentTankVolume) {
        if(currentTankVolume < setPointLow) return MEASUREMENTS.LOW;
        if(currentTankVolume > setPointLow && currentTankVolume < setPointHigh) {
            if(previousWasHigh) {
                previousWasHigh = false;
                return MEASUREMENTS.MID1;
            }
            return MEASUREMENTS.MID;
        }
        if(currentTankVolume > setPointHigh) {
            previousWasHigh = true;
            return MEASUREMENTS.HIGH;
        }
        return MEASUREMENTS.LOW;
    }
}