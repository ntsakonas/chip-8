package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.ConsoleInput;
import ntsakonas.retro.chipate.SystemDisplay;

import java.util.Arrays;
import java.util.Scanner;

public class Chip8System
{
    public interface SystemState
    {
        byte getRegister(int registerNumber);
        void setRegister(int registerNumber, byte value);
        byte readMemory(int address);
        void writeMemory(int address, byte value);
        void setProgramCounter(int address);
        int getProgramCounter();
        void enterSubroutine();
        void returnFromSubroutine();
        void setIndexRegister(int address);
        int getIndexRegister();
        boolean writeVram(byte x, byte y, byte pattern);
        void eraseDisplay();
        boolean isKeyPressed(int keyCode);
        byte waitForKey();
        byte getTimer();
        void setTimer(byte value);
        void setTone(byte value);
        int getDisplayPatternAddress(int i);

        void debug_displayVram();
    }

    private final static byte[] DIGIT_DISPLAY_PATTERNS = new byte[]{
            (byte)0xF0,(byte)0x80,  // E
            (byte)0xF0,(byte)0x80,  // F
            (byte)0xF0,(byte)0x80,  // C
            (byte)0x80,(byte)0x80,
            (byte)0xF0,(byte)0x50,  // B
            (byte)0x70,(byte)0x50,
            (byte)0xF0,(byte)0x50,  // D
            (byte)0x50,(byte)0x50,
            (byte)0xF0,(byte)0x80,  // 5
            (byte)0xF0,(byte)0x10,  // 2
            (byte)0xF0,(byte)0x80,  // 6
            (byte)0xF0,(byte)0x90,  // 8
            (byte)0xF0,(byte)0x90,  // 9
            (byte)0xF0,(byte)0x10,  // 3
            (byte)0xF0,(byte)0x10,
            (byte)0xF0,(byte)0x90,  // A
            (byte)0xF0,(byte)0x90,  // 0
            (byte)0x90,(byte)0x90,
            (byte)0xF0,(byte)0x10,  // 7
            (byte)0x10,(byte)0x10,
            (byte)0x10,(byte)0x60,  // 1 (second half)
            (byte)0x20,(byte)0x20,
            (byte)0x20,(byte)0x70,
            (byte)0xA0,(byte)0xA0,  // 4
            (byte)0xF0,(byte)0x20,
            (byte)0x20
    };

    // LUT for digit patterns (indexed by the digit value)
    private static final int[] DIGIT_DISPLAY_MAPPING = new int[]{
        32, 41, 18, 26, 46, 16, 20, 36, 22, // 0..8
        24, 30,  8, 4,  12, 0,  2           // 9..F
    };

    private final int AVAILABLE_RAM = 4096;//2048;
    private final int RAM_PAGE_SIZE = 256;
    private final int VIDEO_RAM_SIZE = 256;
    private final int PROGRAM_START = 0x200;
    private byte[] ram = new byte[AVAILABLE_RAM];
    private int videoRamBaseAddress;
    private int registersBaseAddress;
    private int stackTop;
    private int stackPointer;
    private int programCounter;
    private int indexRegister;
    private int highestProgramMemoryAddress;
    private int timerTick;
    private RealTimeClock rtc;
    private Keyboard keyboard;
    private SystemState systemState;
    private SystemDisplay systemDisplay;
    private Object videoRamLock = new Object();

    private Object timerLock = new Object();
    private Runnable rtcCallback = () ->
    {
        synchronized (timerLock)
        {
           // System.out.println("tick "+timerTick);
            if (timerTick > 0)
                --timerTick;
        }

        /*
        synchronized (videoRamLock)
        {
            systemDisplay.refresh(Arrays.copyOfRange(ram,videoRamBaseAddress,videoRamBaseAddress+VIDEO_RAM_SIZE));
        }*/
    };

    public Chip8System(Keyboard keyboard,SystemDisplay systemDisplay)
    {
        this.keyboard = keyboard;
        this.systemDisplay = systemDisplay;
        initialiseSystem();
        prepareStateProvider();
    }

    private void initialiseSystem()
    {
        final int numberOfRamPages = AVAILABLE_RAM / RAM_PAGE_SIZE;
        Arrays.fill(ram, (byte) 0);
        // DEBUG: screen pixel addressing is 0-based (x=[0..63] y=[0..31]
        // but some games use 1-based indexing thus going beyond the system memory
        // since the program cannot access the memory directly, for safety and
        // compatibility with those programs, an extra 16 bytes is allocated on
        // top of the VRAM area.
        videoRamBaseAddress = (numberOfRamPages - 1) * RAM_PAGE_SIZE - 16;
        registersBaseAddress = videoRamBaseAddress - 16;
        stackTop = (numberOfRamPages - 2) * RAM_PAGE_SIZE + 0xCF;
        stackPointer = stackTop;
        programCounter = PROGRAM_START;
        highestProgramMemoryAddress = (numberOfRamPages - 2) * RAM_PAGE_SIZE + 0xA0;
        rtc = new RealTimeClock();
        rtc.start(rtcCallback);
        System.out.println(String.format("initialised: total RAM %d bytes, VideoRAM@%04X ,registers@%04X ,Stack@%04X ,PC=%04X , %d bytes available for code",
                AVAILABLE_RAM,videoRamBaseAddress,registersBaseAddress,stackPointer,programCounter,(highestProgramMemoryAddress-programCounter)));
    }

    private void prepareStateProvider()
    {
        systemState = new SystemState()
        {
            @Override
            public byte getRegister(int registerNumber)
            {
                if (registerNumber<0 || registerNumber > 16)
                    throw new IllegalArgumentException(String.format("get register:invalid register access: there is no register %d",registerNumber));
                return ram[registersBaseAddress +registerNumber];
            }

            @Override
            public void setRegister(int registerNumber, byte value)
            {
                if (registerNumber<0 || registerNumber > 16)
                    throw new IllegalArgumentException(String.format("set register:invalid register access: there is no register %d",registerNumber));
                ram[registersBaseAddress +registerNumber] = value;
            }

            @Override
            public byte readMemory(int address)
            {
                // if it is synthetic address, we need to read from the digit pattern table
                if ((address & 0xFFFF0000) == 0xDEAD0000)
                    return DIGIT_DISPLAY_PATTERNS [address & 0xff];
                if (address <0 || address > AVAILABLE_RAM)
                    throw new IllegalArgumentException(String.format("read memory:out of bounds address: cannot read from address %d",address));
                return ram[address];
            }

            @Override
            public void writeMemory(int address, byte value)
            {
                if (address < 0 || address > AVAILABLE_RAM)
                    throw new IllegalArgumentException(String.format("write memory:out of bounds address: cannot write to address %d",address));
                ram[address] = value;
            }

            @Override
            public void setProgramCounter(int address)
            {
                if (address <0 || address > AVAILABLE_RAM)
                    throw new IllegalArgumentException(String.format("execution error :out of bounds address: cannot execute from address %d",address));

                programCounter = address;
            }

            @Override
            public int getProgramCounter()
            {
                return programCounter;
            }

            @Override
            public void enterSubroutine()
            {
                writeMemory(stackPointer--, (byte) (programCounter & 0xFF));
                writeMemory(stackPointer--, (byte) ((programCounter >> 8) & 0xFF));
            }

            @Override
            public void returnFromSubroutine()
            {
                byte msb = readMemory(++stackPointer);
                byte lsb = readMemory(++stackPointer);
                programCounter  = Byte.toUnsignedInt(msb) * 256 + Byte.toUnsignedInt(lsb);
            }

            @Override
            public void setIndexRegister(int address)
            {
                indexRegister = address;
            }

            @Override
            public int getIndexRegister()
            {
                return indexRegister;
            }


            @Override
            public boolean writeVram(byte x, byte y, byte pattern)
            {
                // DEBUG: screen pixel addressing is 0-based (x=[0..63] y=[0..31]
                // but some games use 1-based indexing thus going beyond the system memory
                // it is unknown how the original system would behave but in the simulator
                // out of range values are ignored
                // this has the risk of modifying the behaviour of the program
                // if it relies on the collision
                // instead of this fix, a small expansion of the VRAM is made (see relevant code)
                //if (x < 0 || x > 63 || y < 0 || y > 31)
                //    return false;
                synchronized (videoRamLock)
                {
                    final int SCREEN_WIDTH_PIXELS = 64;
                    final int SCREEN_HEIGHT_PIXELS = 32;
                    int vramOffsetForMSB = videoRamBaseAddress + (SCREEN_WIDTH_PIXELS * y + x) / 8;
                    int vramOffsetForLSB = vramOffsetForMSB + 1;
                    int patternShiftCount = x % 8;
                    if (patternShiftCount == 0)
                    {
                        // x position is aligned to byte in vram
                        byte vramPattern = ram[vramOffsetForMSB];
                        ram[vramOffsetForMSB] = (byte) (vramPattern ^ pattern);
                        refreshDisplay();
                        return  (vramPattern & pattern) !=0;
                    }else
                    {
                        // x position not aligned to byte in vram
                        int patternToShift = Byte.toUnsignedInt(pattern);
                        byte msbPattern = (byte) ((patternToShift >> patternShiftCount) & 0xff);
                        byte lsbPattern = (byte) ((patternToShift << (8 - patternShiftCount)) & 0xff);

                        byte vramPatternMSB = ram[vramOffsetForMSB];
                        ram[vramOffsetForMSB] = (byte) (vramPatternMSB ^ msbPattern);
                        boolean collisionInMsb = ((vramPatternMSB & msbPattern) !=0);
                        boolean collisionInLsb = false;
                        if (vramOffsetForLSB < videoRamBaseAddress + VIDEO_RAM_SIZE)
                        {
                            byte vramPatternLSB = ram[vramOffsetForLSB];
                            ram[vramOffsetForLSB] = (byte) (vramPatternLSB ^ lsbPattern);
                            collisionInLsb =((vramPatternLSB & lsbPattern) !=0);
                        }
                        refreshDisplay();
                        return  collisionInMsb || collisionInLsb;
                    }
                }
            }

            @Override
            public void eraseDisplay()
            {
                synchronized (videoRamLock)
                {
                    for (int i = 0; i < VIDEO_RAM_SIZE; i++)
                        ram[videoRamBaseAddress + i] = 0;
                }
            }

            @Override
            public byte waitForKey()
            {
                return 0;
            }

            @Override
            public boolean isKeyPressed(int keyCode)
            {
               return false;
            }

            @Override
            public byte getTimer()
            {
                synchronized (timerLock)
                {
                    return (byte)(timerTick & 0xff);
                }
            }

            @Override
            public void setTimer(byte value)
            {
                synchronized (timerLock)
                {
                    timerTick = value;
                }
            }

            @Override
            public void setTone(byte value)
            {
                // todo :9mplement tone
            }

            @Override
            public int getDisplayPatternAddress(int digit)
            {
                // this is a synthetic address beyond memory mapping
                return DIGIT_DISPLAY_MAPPING[digit & 0x0F] | 0xDEAD0000;
            }


            @Override
            public void debug_displayVram()
            {
                displayResisters();
                displayVram();
            }
        };
    }

    public void placeRomInMemory(byte[] romBytes)
    {
        for (int romIndex = 0; romIndex < romBytes.length; romIndex++)
            ram[PROGRAM_START+romIndex] = romBytes[romIndex];
    }

    public int getProgramCounter()
    {
        return programCounter;
    }

    public SystemState systemState()
    {
        return systemState;
    }

    public void shutdown()
    {
        rtc.stop();
    }

    private void refreshDisplay()
    {
        systemDisplay.refresh(Arrays.copyOfRange(ram,videoRamBaseAddress,videoRamBaseAddress+VIDEO_RAM_SIZE));
    }

    // Debugging helpers
    // remove them and create a small debugger :-)
    public void singleStep()
    {
        Scanner inputScanner = ConsoleInput.getInput();
        boolean getMoreInput = true;
        while (getMoreInput)
        {
            System.out.print("(BREAK)>");
            String command = inputScanner.nextLine();
            switch (command)
            {
                case "s":
                    getMoreInput = false;
                    break;
                case "r":
                    displayResisters();
                    break;
                case "vr":
                    displayVram();
                    break;
            }
        }
    }


    public void displayResisters()
    {
        System.out.println("Chip-8 status:");
        System.out.println(String.format("pc: %04X   sp:%04X   I:%04X",programCounter,stackPointer,indexRegister));
        for (int i=0;i<4;i++)
        {
            System.out.println(String.format("V%02d = %02X   V%02d = %02X   V%02d = %02X   V%02d = %02X",
                    i*4,systemState.getRegister(i*4),
                    i*4+1,systemState.getRegister(i*4+1),
                    i*4+2,systemState.getRegister(i*4+2),
                    i*4+3,systemState.getRegister(i*4+3)));

        }
        System.out.println();
        System.out.println(String.format("Stack (HH -----> LL) stack top:%04x", stackTop));
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

    public void displayVram()
    {
        //System.out.println("-----------------VRAM-----------------------");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        StringBuilder lineBuffer = new StringBuilder();
        for (int y =0;y<32;y++)
        {
            for (int x = 0; x < 8; x++)
            {
                int vramPattern = Byte.toUnsignedInt(ram[videoRamBaseAddress + 8 * y + x]);
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
