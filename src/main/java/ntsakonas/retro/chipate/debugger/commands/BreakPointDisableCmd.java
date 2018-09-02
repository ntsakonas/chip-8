package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class BreakPointDisableCmd extends DebuggerCommand
{
    private final int address;

    public BreakPointDisableCmd(int address) {
        this.address = address;
    }

    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState)
    {
        debuggerState.getBreakpoints().disableBreakPointAt(address);
    }
}
