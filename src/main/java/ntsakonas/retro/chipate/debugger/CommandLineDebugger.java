package ntsakonas.retro.chipate.debugger;

import ntsakonas.retro.chipate.ConsoleInput;
import ntsakonas.retro.chipate.simulator.Chip8System;

import java.util.Scanner;

public class CommandLineDebugger implements Chip8Debugger
{
    private final int MAX_NUM_OF_PC_BREAKPOINTS = 4;
    int[] pcBreakpoints = new int[MAX_NUM_OF_PC_BREAKPOINTS];
    int numOfPcBreakPoints = 0;
    boolean isSingleStepping = false;
    @Override
    public boolean shouldTakeControl(Chip8System.SystemState systemState)
    {

        // default breakpoint at program start
        if (isSingleStepping || systemState.getProgramCounter() == 0x200)
            return true;
        // keep the initial checks fast otherwise it will slow down execution
        if (numOfPcBreakPoints == 0)
            return false;
        int programCounter = systemState.getProgramCounter();
        for (int bpAddpress:pcBreakpoints)
        {
            if (programCounter == bpAddpress)
                return true;
        }
        return false;
        //return systemState.getProgramCounter() == 0x200;
    }

    @Override
    public void takeControl(Chip8System.SystemState systemState)
    {
        singleStep(systemState);
       // displayRegisters(systemState);
    }

    // Debugging helpers
    // remove them and create a small debugger :-)
    public void singleStep(Chip8System.SystemState systemState)
    {
        System.out.println(String.format("BREAK at pc: %04X",systemState.getProgramCounter()));
        Scanner inputScanner = ConsoleInput.getInput();
        boolean getMoreInput = true;
        while (getMoreInput)
        {
            System.out.print("(STOPPED)>");
            String command = inputScanner.nextLine();
            boolean isBreakPoint = handleBreakpointCommands(command);
            if (isBreakPoint)
                continue;
            switch (command)
            {
                case "s":
                    isSingleStepping = true;
                    getMoreInput = false;
                    break;
                case "r":
                    isSingleStepping = false;
                    getMoreInput = false;
                    break;
                case "sr":
                    displayRegisters(systemState);
                    break;
                case "vr":
                    displayVram(systemState);
                    break;
            }
        }
    }

    // handles all breakpoints commands
    // examples
    // bpa 512
    // bpa 0x200
    // bpr 512
    // bpr 0x200
    private boolean handleBreakpointCommands(String command)
    {
        String cmd = command.toLowerCase();
        boolean isListBP = cmd.startsWith("bpl");

        if (isListBP)
        {
            listBreakpoints();
            return true;
        }

        boolean isAddBP = cmd.startsWith("bpa");
        boolean isDeleteBP = cmd.startsWith("bpr");
        boolean isEnableBP = cmd.startsWith("bpe");
        boolean isDisableBP = cmd.startsWith("bpd");
        if (isAddBP || isDeleteBP || isEnableBP || isDisableBP)
        {
            int programmLocation = extractBreakPointAddress(cmd);
            if (isAddBP)
                addNewBreakPointAt(programmLocation);
            if (isDeleteBP)
                deleteBreakPointAt(programmLocation);
            return true;
        }
        return false;
    }

    private void listBreakpoints()
    {
        if (numOfPcBreakPoints == 0)
        {
            System.out.println("No breakpoints");
            return;
        }
        System.out.println("Current breakpoints");
        System.out.println("ADDR ENABLED");
        for (int i = 0; i < numOfPcBreakPoints; i++)
            System.out.println(String.format("%04X %c",pcBreakpoints[i],'Y'));
    }

    private void deleteBreakPointAt(int programLocation)
    {
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            if (pcBreakpoints[i] == programLocation)
            {
               // erase breakpoint by moving the last breakpoint in this location
                numOfPcBreakPoints--;
                pcBreakpoints[i] = pcBreakpoints[numOfPcBreakPoints];
                pcBreakpoints[numOfPcBreakPoints] = 0;
                return;
            }
        }
        System.out.println("Could not find this breakpoint.");
    }
    private void addNewBreakPointAt(int programLocation)
    {
        for (int i=0; i<numOfPcBreakPoints;i++)
        {
            if (pcBreakpoints[i] == programLocation)
            {
                System.out.println("Breakpoint already set at this location.");
                return;
            }
        }
        if (numOfPcBreakPoints < MAX_NUM_OF_PC_BREAKPOINTS)
            pcBreakpoints[numOfPcBreakPoints++] = programLocation;
        else
            System.out.println("Cannot add more breakpoints.");
    }

    private int extractBreakPointAddress(String cmd)
    {
        int radix = cmd.substring(4).startsWith("0x") ? 16 : 10;
        String location = radix == 10 ? cmd.substring(4) : cmd.substring(6);
        return Integer.valueOf(location, radix);
    }

    public void displayRegisters(Chip8System.SystemState systemState)
    {
        byte[] ram = systemState.getRam();
        System.out.println("Chip-8 status:");
        System.out.println(String.format("pc: %04X   sp:%04X   I:%04X",systemState.getProgramCounter(),systemState.getStackPointer(),systemState.getIndexRegister()));
        for (int i=0;i<4;i++)
        {
            System.out.println(String.format("V%02d = %02X   V%02d = %02X   V%02d = %02X   V%02d = %02X",
                    i*4,systemState.getRegister(i*4),
                    i*4+1,systemState.getRegister(i*4+1),
                    i*4+2,systemState.getRegister(i*4+2),
                    i*4+3,systemState.getRegister(i*4+3)));

        }
        System.out.println();
        int stackTop = systemState.getStackTop();
        System.out.println(String.format("Stack (HH -----> LL) stack top:%04X", stackTop));
        StringBuilder stackDump = new StringBuilder();
        for (int i=0;i<48;i++)
        {
            if (i % 8 == 0)
                stackDump.append("\n");
            stackDump.append(String.format("%02X", ram[stackTop - i])).append(" ");
        }
        System.out.println(stackDump.toString());
        System.out.println("---------------------------------------------");
    }

    public void displayVram(Chip8System.SystemState systemState)
    {
        //System.out.println("-----------------VRAM-----------------------");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        byte[] videoRam = systemState.getVideoRam();
        StringBuilder lineBuffer = new StringBuilder();
        for (int y =0;y<32;y++)
        {
            for (int x = 0; x < 8; x++)
            {
                //int vramPattern = Byte.toUnsignedInt(ram[videoRamBaseAddress + 8 * y + x]);
                int vramPattern = Byte.toUnsignedInt(videoRam[8 * y + x]);
                int vramPatternMask = 0x80;
                for (int pixel=0;pixel<8;pixel++)
                {
                    if ((vramPattern & vramPatternMask) == vramPatternMask)
                        lineBuffer.append("*");
                    else
                        lineBuffer.append(".");
                    vramPatternMask >>=1;
                }
            }
            System.out.println(lineBuffer.toString());
            lineBuffer.setLength(0);
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

    }
}
