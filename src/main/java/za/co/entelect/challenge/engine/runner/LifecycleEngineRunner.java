package za.co.entelect.challenge.engine.runner;

public interface LifecycleEngineRunner {

    void onGameStarting() throws Exception;

    void onRoundStarting();

    void onProcessRound() throws Exception;

    void onRoundComplete();

    void onGameComplete();
}
