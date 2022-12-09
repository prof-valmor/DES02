package model.diagnoser;

public interface DiagnoserListener {
    /** @param diagnoserState it is the diagnoser state in case a coordinator
     * wants to take decisions based on that information. */
    void onFailureState(Diagnoser.State diagnoserState);
    void onFailureOff();
}
