package ntsakonas.retro.chipate.debugger;

import ntsakonas.retro.chipate.ConsoleInput;
import ntsakonas.retro.chipate.debugger.commands.DebuggerCommand;
import ntsakonas.retro.chipate.instructions.ChipInstruction;
import ntsakonas.retro.chipate.instructions.Instructions;
import ntsakonas.retro.chipate.simulator.Chip8System;

import java.util.Scanner;

public class CommandLineDebugger implements Chip8Debugger, Chip8Debugger.DebuggerState {

    private final int NUM_OF_INSTRUCTION_TO_DECOMPILE = 6;
    Instructions.Parser instructionParser = Instructions.parser();
    boolean shouldTakeControl = false;
    boolean isDebuggerAttached = false;
    DebugBreakpoints breakpoints;

    public CommandLineDebugger() {
        breakpoints = new DebugBreakpoints();
    }

    @Override
    public boolean isDebuggerAttached() {
        return isDebuggerAttached;
    }

    @Override
    public void active(boolean isActive) {
        isDebuggerAttached = isActive;
    }

    @Override
    public boolean shouldTakeControl(Chip8System.SystemState systemState) {
        // default breakpoint at program start
        if (shouldTakeControl || systemState.getProgramCounter() == 0x200)
            return true;
        // keep the initial checks fast otherwise it will slow down execution
        if (!breakpoints.hasBreakpoints())
            return false;
        if (breakpoints.reachedBreakpoint(systemState.getProgramCounter()))
            return true;
        return false;
    }

    @Override
    public void takeControl(Chip8System.SystemState systemState) {
        singleStep(systemState);
    }

    private void singleStep(Chip8System.SystemState systemState) {
        System.out.println(String.format("BREAK at address %04X", systemState.getProgramCounter()));
        //disassemble a few commands from current PC to create context
        disassembleProgramAtAddress(systemState);
        Scanner inputScanner = ConsoleInput.getInput();
        boolean getMoreInput = true;
        while (getMoreInput) {
            System.out.print("(PAUSED)>");
            try {
                String command = inputScanner.nextLine().toLowerCase();
                if (command.isEmpty()) {
                    disassembleProgramAtAddress(systemState);
                    continue;
                }
                DebuggerCommand debuggerCommand = DebuggerCommand.fromInput(command);
                debuggerCommand.execute(systemState, this);
                shouldTakeControl = debuggerCommand.shouldTakeControlAfterNextCommand();
                if (debuggerCommand.shouldResumeExecution())
                    break;
            } catch (Throwable e) {
                System.out.println("what??? I don't know that command.");
            }
        }
    }

    private void disassembleProgramAtAddress(Chip8System.SystemState systemState) {
        // disassemble the next 5 commands
        int programCounter = systemState.getProgramCounter();
        byte[] ram = systemState.getRam();
        for (int i = 0; i < NUM_OF_INSTRUCTION_TO_DECOMPILE; i++) {
            ChipInstruction instruction = instructionParser.decode(programCounter, ram[programCounter], ram[programCounter + 1]);
            System.out.println(String.format("  %04X  %S  %S", instruction.getAddress(), instruction.getOpcodes(), instruction.getMnemonic()));
            programCounter += 2;
        }
    }

    @Override
    public DebugBreakpoints getBreakpoints() {
        return breakpoints;
    }
}
