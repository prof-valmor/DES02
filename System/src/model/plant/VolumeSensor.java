package model.plant;

/**
 * Sensor that observes the volume of a tank.
 */
public interface VolumeSensor {
    void updateCurrentMeasurement(double volume);
    double getCurrentMeasurement();
    void activateFailure();
    void deactivateFailure();
}
