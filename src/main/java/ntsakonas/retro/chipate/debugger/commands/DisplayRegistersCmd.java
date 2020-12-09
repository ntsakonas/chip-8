package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class DisplayRegistersCmd extends DebuggerCommand {

    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState) {
        byte[] ram = systemState.getRam();
        System.out.println("Chip-8 status:");
        System.out.println(String.format("pc: %04X   sp:%04X   I:%04X", systemState.getProgramCounter(), systemState.getStackPointer(), systemState.getIndexRegister()));
        for (int i = 0; i < 4; i++) {
            System.out.println(String.format("V%02d = %02X   V%02d = %02X   V%02d = %02X   V%02d = %02X",
                    i * 4, systemState.getRegister(i * 4),
                    i * 4 + 1, systemState.getRegister(i * 4 + 1),
                    i * 4 + 2, systemState.getRegister(i * 4 + 2),
                    i * 4 + 3, systemState.getRegister(i * 4 + 3)));

        }
        System.out.println();
        int stackTop = systemState.getStackTop();
        System.out.println(String.format("Stack (HH -----> LL) stack top:%04X", stackTop));
        StringBuilder stackDump = new StringBuilder();
        for (int i = 0; i < 48; i++) {
            if (i % 8 == 0)
                stackDump.append("\n");
            stackDump.append(String.format("%02X", ram[stackTop - i])).append(" ");
        }
        System.out.println(stackDump.toString());
        System.out.println("---------------------------------------------");
    }
}
