package model.plant;

public class Flowmeter {
    public static double getFlow(double volumeOnTheTank) {
        double response = 1.0;
        if(volumeOnTheTank > 200) response = 3.0;
        else if(volumeOnTheTank > 100) response = 2.0;
        else if(volumeOnTheTank > 50) response = 1.0;
        else response = 0.5;
        
        return response;
    }
}
