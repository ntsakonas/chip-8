package ntsakonas.retro.chipate.debugger;

import ntsakonas.retro.chipate.simulator.Chip8System;

public interface Chip8Debugger
{

    boolean shouldTakeControl(Chip8System.SystemState systemState);

    void takeControl(Chip8System.SystemState systemState);
}
