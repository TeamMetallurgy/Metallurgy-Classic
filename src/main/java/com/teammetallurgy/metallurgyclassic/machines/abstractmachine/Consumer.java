package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

import org.jetbrains.annotations.Nullable;

public interface Consumer<T, S> extends AbstractComponentInterface {
    enum State {
        IDLE,
        PROCESSING,
        DONE
    }

    @Nullable S consume(boolean canContinue);

    boolean canConsume();

    @Nullable S getResult();
}
