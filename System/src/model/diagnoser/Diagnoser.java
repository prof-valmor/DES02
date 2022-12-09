package model.diagnoser;

import model.control.ControlListener;
import model.control.SimpleControl;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Diagnoser implements ControlListener {
    private Timer timer;
    private Task theTask;
    private static Diagnoser instance;

    /**
     * States based on the generated diagnoser automata.
     */
    public enum State {
        NONE, _1N2F,  _3N4F,   _5N6F, _7N9F,
        _8N10F, _11N12F, _2F,   _4F,
        _6F,    _9F,    _10F,   _12F
    }

    private Diagnoser() {
        timer = new Timer();
        theTask = new Task();
        timer.scheduleAtFixedRate(theTask, 500, 1000);
    }

    public static Diagnoser getInstance() {
        if(instance == null) instance = new Diagnoser();
        return instance;
    }

    @Override
    public void onEvent(SimpleControl.Event event) {
        theTask.event = event;
    }

    public void addListener(DiagnoserListener listener) {
        theTask.addListeners(listener);
    }
}

class Task extends TimerTask {
    /** current state */
    private Diagnoser.State state;
    /** State to move to */
    public SimpleControl.Event event;

    ArrayList<DiagnoserListener> listeners = new ArrayList<>(1);

    public void addListeners(DiagnoserListener listener) {
        if(!listeners.contains(listener))
            this.listeners.add(listener);
    }

    public Task() {
        state = Diagnoser.State._1N2F;
        event = null;
    }

    @Override
    public void run() {
        switch(state) {
            case _1N2F -> {
                if(event == SimpleControl.Event.InOpen) state = Diagnoser.State._3N4F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._2F;
            }
            case _3N4F -> {
//                if(event == SimpleControl.Event.HSup) state = Diagnoser.State._5N6F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._4F;
            }
            case _5N6F -> {
                if(event == SimpleControl.Event.InClose) state = Diagnoser.State._7N9F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._6F;
            }
            case _7N9F -> {
                if(event == SimpleControl.Event.OutOpen) state = Diagnoser.State._8N10F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._9F;
            }
            case _8N10F -> {
//                if(event == SimpleControl.Event.HInf) state = Diagnoser.State._11N12F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._10F;
            }
            case _11N12F -> {
                if(event == SimpleControl.Event.OutClose) state = Diagnoser.State._1N2F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._12F;
            }
            case _2F -> {
                if(event == SimpleControl.Event.InOpen) state = Diagnoser.State._4F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._2F;
            }
            case _4F -> {
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._4F;
            }
            case _6F -> {
                if(event == SimpleControl.Event.InClose) state = Diagnoser.State._9F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._6F;
            }
            case _9F -> {
                if(event == SimpleControl.Event.OutOpen) state = Diagnoser.State._10F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._9F;
            }
            case _10F -> {
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._10F;
            }
            case _12F -> {
                if(event == SimpleControl.Event.OutClose) state = Diagnoser.State._2F;
//                if(event == SimpleControl.Event.HMax) state = Diagnoser.State._12F;
            }
        }

        // If it enters on any state of the set [_2F, _4F, _6F, _9F, _10F, _12F] it means the control is in failure.
        informListeners();
    }
    private Diagnoser.State newState = Diagnoser.State.NONE;
    public void informListeners() {
        if(state != newState) {
            switch (state) {
                case _2F:
                case _4F:
                case _6F:
                case _9F:
                case _10F:
                case _12F:
                    for (DiagnoserListener l : listeners) {
                        l.onFailureState(state);
                    }
                    break;
                default:
                    for (DiagnoserListener l : listeners) {
                        l.onFailureOff();
                    }
                    break;

            }
            newState = state;
        }
    }
}