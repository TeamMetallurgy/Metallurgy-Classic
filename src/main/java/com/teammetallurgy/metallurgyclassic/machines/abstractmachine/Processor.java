package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import java.util.function.Function;

public interface Processor extends AbstractComponentInterface {

    interface Listener {
        void onStateChange(boolean state);
    }

    enum State {
        IDLE,
        PROCESSING,
        DONE
    }

    State state();

    void process(boolean canContinue);

    boolean canProcess();

    void addStateChangeListener(Listener listener);
}
