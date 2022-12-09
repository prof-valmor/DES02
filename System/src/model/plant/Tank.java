package model.plant;

import java.util.Vector;

public class Tank {
    private double currentVolume;
    private double totalVolume;
    private Vector<VolumeSensor> listOfVolumeSensor;
    private SimpleSensor tankFullSensor;
    private SimpleSensor tankEmptySensor;

    public Tank(double totalVolume) {
        this.totalVolume = totalVolume;
        this.currentVolume = 0.0;
        listOfVolumeSensor = new Vector<>(1);
    }

    public void add(double volumeToAdd) {
        currentVolume += volumeToAdd;
        this.onVolumeUpdate();
    }

    public void remove(double volumeToRemove) {
        currentVolume -= volumeToRemove;
        if(currentVolume < 0) currentVolume = 0;
        this.onVolumeUpdate();
    }

    public void addVolumeSensor(VolumeSensor sensor) {
        if(!listOfVolumeSensor.contains(sensor)) listOfVolumeSensor.add(sensor);
    }
    public void setTankFullSensor(SimpleSensor sensor) {
        this.tankFullSensor = sensor;
    }
    public void setTankEmptySensor(SimpleSensor sensor) {
        this.tankEmptySensor = sensor;
    }

    private void onVolumeUpdate() {
        for(VolumeSensor s : listOfVolumeSensor) s.updateCurrentMeasurement(this.currentVolume);
        //
        if(this.currentVolume >= totalVolume) {
            if(this.tankFullSensor != null) this.tankFullSensor.onSensorActivated();
        }
        else if(this.currentVolume < totalVolume && this.currentVolume > 0) {
            if(this.tankFullSensor != null) this.tankFullSensor.onSensorInactivated();
            if(this.tankEmptySensor != null) this.tankEmptySensor.onSensorInactivated();
        }
        else if(this.currentVolume == 0) {
            if(this.tankEmptySensor != null) this.tankEmptySensor.onSensorActivated();
        }
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public double getRealVolume() {
        return currentVolume;
    }
}
