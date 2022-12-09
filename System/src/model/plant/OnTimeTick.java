package model.plant;

public interface OnTimeTick {
    void onTimePass(int secondsSinceLastCall);
}
