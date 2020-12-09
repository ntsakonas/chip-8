package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public abstract class DebuggerCommand {

    private static int extractBreakPointAddress(String cmd) {
        return Integer.valueOf(cmd.substring(3).trim(), 16);
    }

    private static int extractRamDumpAddress(String cmd) {
        return Integer.valueOf(cmd.substring(2).trim(), 16);
    }

    public static DebuggerCommand fromInput(String command) {
        if (command.equals("s"))
            return new SingleStepCommand();
        else if (command.equals("r"))
            return new ResumeExecutionCommand();
        else if (command.equals("sr"))
            return new DisplayRegistersCmd();
        else if (command.equals("sv"))
            return new DisplayVideoRamCmd();
        else if (command.startsWith("sm"))
            return new DumpRamCmd(extractRamDumpAddress(command));
        else if (command.startsWith("bpl"))
            return new BreakPointListCmd();
            // all breakpoints commands expect the address in hex
            // e.g
            // "bpa200" or "bpa 200"
            // "bpr 200"
            // etc
        else if (command.startsWith("bpa"))
            return new BreakPointAddCmd(extractBreakPointAddress(command));
        else if (command.startsWith("bpr"))
            return new BreakPointDeleteCmd(extractBreakPointAddress(command));
        else if (command.startsWith("bpe"))
            return new BreakPointEnableCmd(extractBreakPointAddress(command));
        else if (command.startsWith("bpd"))
            return new BreakPointDisableCmd(extractBreakPointAddress(command));
            // TODO:: add new commands
        /*
           case "cr":
           System.out.println("add change register commands 'cr 1 1F' or 'cr pc/sp/I 2F40'");
           break;
           case "cm":
           System.out.println("add change memory commands 'cm 0200 FB'");
           break;
         */
        else return new UnknownCmd();
    }

    public abstract void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState);

    public boolean shouldResumeExecution() {
        return false;
    }

    public boolean shouldTakeControlAfterNextCommand() {
        return false;
    }
}
