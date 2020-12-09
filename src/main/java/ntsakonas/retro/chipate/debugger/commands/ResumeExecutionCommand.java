package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class ResumeExecutionCommand extends DebuggerCommand {

    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState) {
        // no op
    }

    @Override
    public boolean shouldResumeExecution() {
        return true;
    }
}
