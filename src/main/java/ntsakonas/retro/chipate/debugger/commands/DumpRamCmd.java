package ntsakonas.retro.chipate.debugger.commands;

import ntsakonas.retro.chipate.debugger.Chip8Debugger;
import ntsakonas.retro.chipate.simulator.Chip8System;

public class DumpRamCmd extends DebuggerCommand {

    private final int startAddress;

    public DumpRamCmd(int startAddress) {
        this.startAddress = startAddress;
    }

    @Override
    public void execute(Chip8System.SystemState systemState, Chip8Debugger.DebuggerState debuggerState) {
        System.out.println("ADDR --------------------- HEX --------------------- ----- ASCII ----");

        byte[] videoRam = systemState.getRam();
        StringBuilder hexPart = new StringBuilder();
        StringBuilder asciiPart = new StringBuilder();
        // dump 16 pages with 16 bytes each
        final int NUM_OF_PAGES = 16;
        final int NUM_OF_BYTES_IN_PAGE = 16;

        boolean endOfRamReached = false;
        for (int page = 0; page < NUM_OF_PAGES; page++) {
            int pageStartAddress = startAddress + page * NUM_OF_BYTES_IN_PAGE;
            for (int pageOffset = 0; pageOffset < NUM_OF_BYTES_IN_PAGE; pageOffset++) {
                try {
                    byte memoryValue = videoRam[pageStartAddress + pageOffset];
                    hexPart.append(String.format("%02X ", Byte.toUnsignedInt(memoryValue)).toUpperCase());
                    asciiPart.append(Character.isLetterOrDigit(memoryValue) ? (char) memoryValue : '.');
                } catch (ArrayIndexOutOfBoundsException e) {
                    endOfRamReached = true;
                    break;
                }
            }
            System.out.print(String.format("%04X ", pageStartAddress));
            System.out.print(String.format("%-48s", hexPart.toString()));//hexPart.toString());
            System.out.println(asciiPart.toString());
            hexPart.setLength(0);
            asciiPart.setLength(0);
            if (endOfRamReached)
                break;
        }

    }
}
