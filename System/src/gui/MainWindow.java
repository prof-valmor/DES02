package gui;

import model.control.FailuteModeControlListener;
import model.diagnoser.Diagnoser;
import model.diagnoser.DiagnoserListener;
import model.plant.OnPlantUpdateListener;
import model.plant.SystemPlant;
import model.plant.ValveState;
import model.control.SimpleControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements ActionListener, FailuteModeControlListener {
    private JButton btON, btValveOn, btOtherValveOn, btControlOn, btFailureOn;
    private JLabel label, label2;
    private  JProgressBar tank;
    private JLabel lbInValve, lbOutValve, lbCtrlState, lbFailureModeState;

    public MainWindow () {
        super("Planta");
        initComponents();
        //
        SystemPlant.getInstance().setListener(new OnPlantUpdateListener() {
            @Override
            public void onPlantUpdate(SystemPlant plant) {
//                double volume = plant.getTheSensor().getCurrentMeasurement();
                double volume = plant.getTheTank().getRealVolume();
                double totalVolume = plant.getTheTank().getTotalVolume();
                tank.setValue((int)volume);
                label.setText(String.format("%.2f", volume) + " L - " + String.format("%.2f", (volume/totalVolume) * 100)+" % ");
                lbInValve.setText("InValve: " + plant.getInputValve().getState());
                lbOutValve.setText("OutValve: " + plant.getOutputValve().getState());
                lbCtrlState.setText("Ctrl State: " + SimpleControl.getInstance().getState());
            }
        });
        //
        Diagnoser.getInstance().addListener(new DiagnoserListener() {
            @Override
            public void onFailureState(Diagnoser.State diagnoserState) {
                label2.setText("Failure: " + diagnoserState);
            }

            @Override
            public void onFailureOff() {
                label2.setText("Failure Off...");
            }
        });
    }


    private void initComponents() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new GridLayout(3,1));
        add(header, BorderLayout.NORTH);

        header.add(btON = new JButton("ON"));
        header.add(label = new JLabel("volume:"));
        header.add(label2 = new JLabel("Diagnostic State: "));
        label.setHorizontalAlignment(JLabel.CENTER);
        label2.setHorizontalAlignment(JLabel.CENTER);

        btON.addActionListener(this);
        setSize(500, 300);
        tank = new JProgressBar();
        tank.setBackground(Color.CYAN);
        tank.setMaximum((int)SystemPlant.getInstance().getTheTank().getTotalVolume());
        tank.setOrientation(JProgressBar.VERTICAL);
        add(tank, BorderLayout.CENTER);

        JPanel east = new JPanel(new GridLayout(4,1));
        add(east, BorderLayout.EAST);
        east.add(btValveOn = new JButton("V1 ON"));
        btValveOn.addActionListener(this);
        east.add(btOtherValveOn = new JButton("V2 ON"));
        btOtherValveOn.addActionListener(this);
        east.add(btControlOn = new JButton("Control ON"));
        east.add(btFailureOn = new JButton("Failure ON"));
        btControlOn.addActionListener(this);
        btFailureOn.addActionListener(this);
        //
        JPanel west = new JPanel(new GridLayout(4, 1));
        add(west, BorderLayout.WEST);
        west.add(lbInValve = new JLabel("InValve: "));
        west.add(lbOutValve = new JLabel("OutValve: "));
        west.add(lbCtrlState = new JLabel("Ctrl State: "));
        west.add(lbFailureModeState = new JLabel("FM State: "));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == btON) {
            if(btON.getText().equalsIgnoreCase("ON")) {
                btON.setText("OFF");
                SystemPlant.getInstance().turnOn();
            }
            else {
                btON.setText("ON");
                SystemPlant.getInstance().turnOff();
            }
        }
        else if(source == btValveOn) {
            if(SystemPlant.getInstance().getInputValve().getState() == ValveState.OPEN) {
                SystemPlant.getInstance().getInputValve().closeIt();
                btValveOn.setText("V1 ON");
            }
            else {
                SystemPlant.getInstance().getInputValve().openIt();
                btValveOn.setText("V1 OFF");
            }
        }
        else if(source == btOtherValveOn) {
            if(SystemPlant.getInstance().getOutputValve().getState() == ValveState.OPEN) {
                SystemPlant.getInstance().getOutputValve().closeIt();
                btOtherValveOn.setText("V2 ON");
            }
            else {
                SystemPlant.getInstance().getOutputValve().openIt();
                btOtherValveOn.setText("V2 OFF");
            }
        }
        else if(source == btControlOn) {
            if(SimpleControl.getInstance().isOn()) {
                SimpleControl.getInstance().turnOff();
                btControlOn.setText("Control ON");
            }
            else {
                SimpleControl.getInstance().turnON();
                btControlOn.setText("Control OFF");
            }
        }
        else if(source == btFailureOn) {
            if(btFailureOn.getText().equalsIgnoreCase("Failure ON")) {
                btFailureOn.setText("Failure OFF");
                SystemPlant.getInstance().getTheSensor().activateFailure();
            }
            else {
                btFailureOn.setText("Failure ON");
                SystemPlant.getInstance().getTheSensor().deactivateFailure();
            }

        }
    }

    @Override
    public void onStateChange(String state) {
        lbFailureModeState.setText("FM State: " + state);
    }
}
