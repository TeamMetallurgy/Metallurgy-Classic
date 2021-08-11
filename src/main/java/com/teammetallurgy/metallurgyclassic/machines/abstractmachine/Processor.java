package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

public interface Processor extends AbstractComponentInterface {
    enum State {
        IDLE,
        PROCESSING,
        DONE
    }

    State state();

    void process(boolean canContinue);

    boolean canProcess();
}
