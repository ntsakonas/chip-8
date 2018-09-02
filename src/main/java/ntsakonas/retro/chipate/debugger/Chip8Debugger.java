package ntsakonas.retro.chipate.debugger;

import ntsakonas.retro.chipate.simulator.Chip8System;

public interface Chip8Debugger extends Chip8System.SystemStateObserver
{
    interface DebuggerState
    {
        DebugBreakpoints getBreakpoints();
    }

    @Override
    default void observe(Chip8System.SystemState systemState)
    {
        if (isDebuggerAttached())
        {
            if (shouldTakeControl(systemState))
                takeControl(systemState);
        }
    }

    boolean isDebuggerAttached();

    boolean shouldTakeControl(Chip8System.SystemState systemState);

    void takeControl(Chip8System.SystemState systemState);
}
