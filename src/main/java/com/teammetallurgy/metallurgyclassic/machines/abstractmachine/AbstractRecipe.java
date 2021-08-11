package com.teammetallurgy.metallurgyclassic.machines.abstractmachine;

public class AbstractRecipe<R, T> {
    public R input;
    public T output;
    public int processingTime;

    public AbstractRecipe(R input, T output, int processingTime) {
        this.input = input;
        this.output = output;
        this.processingTime = processingTime;
    }
}
