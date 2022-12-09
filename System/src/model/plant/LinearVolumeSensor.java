package model.plant;

public class LinearVolumeSensor implements VolumeSensor{
    private double currentVolumeReading;
    private boolean inFailureMode;

    public LinearVolumeSensor() {
        this.currentVolumeReading = 0;
        this.inFailureMode = false;
    }
    public void activateFailure() {
        inFailureMode = true;
    }
    public void deactivateFailure() {
        inFailureMode = false;
    }
    @Override
    public void updateCurrentMeasurement(double volume) {
        if(inFailureMode) {
            this.currentVolumeReading = Double.MAX_VALUE;
        }
        else {
            this.currentVolumeReading = volume;
        }
    }

    @Override
    public double getCurrentMeasurement() {
        return currentVolumeReading;
    }

    public double getCurrentVolumeReading() {
        return currentVolumeReading;
    }
}
