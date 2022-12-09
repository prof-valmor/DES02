package model.plant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a Tank which has an input and an output valves.
 * A sensor can be added to the tank.
 */
public class SystemPlant {
    private static SystemPlant instance;
    private Tank  theTank;
    private Valve inputValve;
    private Valve outputValve;
    private VolumeSensor theSensor;
    private boolean isOn;
    //
    private Timer timer;
    private OnPlantUpdateListener listener;

    public static SystemPlant getInstance() {
        if(instance == null) instance = new SystemPlant();
        return instance;
    }

    private SystemPlant() {
        theTank     = new Tank(300.0);
        inputValve  = new Valve(5.23);
        outputValve = new Valve(5.22);
        theSensor   = new LinearVolumeSensor();
        theTank.addVolumeSensor(theSensor);
        //
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(isOn) {
                    inputValve.onTimePass(1);
                    outputValve.onTimePass(1);
                    //
                    if(listener != null) listener.onPlantUpdate(instance);
                }
            }
        }, 0, 1000);
        //
        inputValve.addListener(volumeDispensed -> {
            theTank.add(volumeDispensed);
        });
        outputValve.addListener((volumeDispensed -> {
            theTank.remove(volumeDispensed);
        }));
    }

    public VolumeSensor getTheSensor() {
        return theSensor;
    }

    public Tank getTheTank() {
        return theTank;
    }

    public void turnOn() {
        this.isOn = true;
    }
    public void turnOff() {
        this.isOn = false;
    }

    public Valve getInputValve() {
        return inputValve;
    }

    public Valve getOutputValve() {
        return outputValve;
    }

    public void setListener(OnPlantUpdateListener listener) {
        this.listener = listener;
    }

    public double getVolumePercentage() {
        double percentage = theSensor.getCurrentMeasurement();
        percentage /= theTank.getTotalVolume();

        return percentage;
    }
}
