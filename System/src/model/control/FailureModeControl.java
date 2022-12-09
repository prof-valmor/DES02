package model.control;

import gui.MainWindow;
import model.plant.SystemPlant;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FailureModeControl {
    private Timer timer;
    private TheTask task;
    public FailureModeControl(SystemPlant plant) {
        timer = new Timer("FailureModeControl");
        task = new TheTask(plant);
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void turnON() {
        task.isON = true;
    }
    public void turnOff() {
        task.isON = false;
    }

    public void addListener(FailuteModeControlListener listener) {
        task.addListener(listener);
    }
}

class TheTask extends TimerTask {
    SystemPlant plant;
    boolean isON;
    ArrayList<FailuteModeControlListener> listeners;

    public void addListener(FailuteModeControlListener listener) {
        if(listeners == null) listeners = new ArrayList<>(1);
        listeners.add(listener);
    }

    public enum STATES {
        UM, DOIS, TRES, QUATRO, CINCO, SEIS, SETE, OITO, NOVE, DEZ
    }
    STATES state;
    private int timeCounter;
    private static final int adjustTime = 50; //s
    private static final int onTime = 32;
    private static final int offTime = 20;

    TheTask(SystemPlant plant) {
        this.plant = plant;
        isON = false;
        state = STATES.UM;
    }

    @Override
    public void run() {
        if(!isON) return;

        switch (state) {
            case UM -> {
                plant.getInputValve().closeIt();
                state = STATES.DOIS;
            }
            case DOIS -> {
                plant.getOutputValve().openIt();
                state = STATES.TRES;
                timeCounter = adjustTime;
            }
            case TRES -> {
                timeCounter--;
                if(timeCounter == 0)
                    state = STATES.QUATRO;
            }
            case QUATRO -> {
                plant.getOutputValve().closeIt();
                state = STATES.CINCO;
            }
            case CINCO -> {
                plant.getInputValve().openIt();
                state = STATES.SEIS;
                timeCounter = onTime;
            }
            case SEIS -> {
                timeCounter--;
                if(timeCounter == 0)
                    state = STATES.SETE;
            }
            case SETE -> {
                plant.getInputValve().closeIt();
                state = STATES.OITO;
            }
            case OITO -> {
                plant.getOutputValve().openIt();
                state = STATES.NOVE;
                timeCounter = offTime;
            }
            case NOVE -> {
                timeCounter--;
                if(timeCounter == 0) {
                    state = STATES.QUATRO;
                }
            }
//            case DEZ -> {
//                plant.getOutputValve().closeIt();
//                state = STATES.CINCO;
//            }
        }
        for(FailuteModeControlListener l : listeners) {
            l.onStateChange(state.name() + " - " + timeCounter);
        }
    }
}
