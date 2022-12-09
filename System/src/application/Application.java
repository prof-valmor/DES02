package application;

import gui.MainWindow;
import model.control.FailureModeControl;
import model.control.SimpleControl;
import model.coordinator.Coordinator;
import model.diagnoser.Diagnoser;
import model.diagnoser.DiagnoserListener;
import model.plant.SystemPlant;

public class Application {
    static Coordinator coordinator;
    static SystemPlant plant;
    static SimpleControl control;
    static Diagnoser diagnoser;
    static FailureModeControl failureModeControl;

    public static void main(String[] args) {
        System.out.println(">> Starting it...");
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        //
        // Initializing system components.
        plant   = SystemPlant.getInstance();
        control = SimpleControl.getInstance();
        control.initialize(plant);
        control.defineSetPoints(200, 210);
        diagnoser = Diagnoser.getInstance();
        control.addListener(diagnoser);
        failureModeControl = new FailureModeControl(plant);
        failureModeControl.addListener(mainWindow);
        // adding some coordination to act in case of failure.
//        coordinator = new Coordinator();
//        diagnoser.addListener(coordinator);
        diagnoser.addListener(new DiagnoserListener() {
            @Override
            public void onFailureState(Diagnoser.State diagnoserState) {
                // Failure has happen. Switch to a different controller.
                control.turnOff();
                failureModeControl.turnON();
            }

            @Override
            public void onFailureOff() {
                failureModeControl.turnOff();
                control.turnON();
            }
        });

    }
}
