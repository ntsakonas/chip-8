package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.debugger.DebugBreakpoints;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class BreakPointListCmd extends DebuggerCommand
{
    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState)
    {
        DebugBreakpoints breakpoints = debuggerState.getBreakpoints();
        if (breakpoints.hasBreakpoints())
            breakpoints.listBreakpoints();
        else
            System.out.println("No breakpoints");
    }
}
