package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

class SingleStepCommand extends DebuggerCommand
{
    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState)
    {
        // no op, it allows the program to run but will take control after the next command
    }

    @Override
    public boolean shouldResumeExecution()
    {
        return true;
    }

    @Override
    public boolean shouldTakeControlAfterNextCommand()
    {
        return true;
    }
}
