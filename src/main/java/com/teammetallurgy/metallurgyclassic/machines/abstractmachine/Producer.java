package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

public interface Producer<T> extends AbstractComponentInterface {

    void produce(boolean canContinue, T output);

    boolean canProduce(T output);
}
