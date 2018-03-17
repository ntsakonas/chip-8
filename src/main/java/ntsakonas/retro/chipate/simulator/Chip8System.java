package ntsakonas.retro.chipate.simulator;

import java.util.Arrays;
import java.util.Locale;
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
        byte getKey();
        byte getTimer();
        void setTimer(byte value);
        void setTone(byte value);
        int getDisplayPatternAddress(int i);
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

    private final int AVAILABLE_RAM = 2048;
    private final int RAM_PAGE_SIZE = 256;
    private final int PROGRAM_START = 0x200;
    private byte[] ram = new byte[AVAILABLE_RAM];
    private int videoRamBaseAddress;
    private int registersBaseAddress;
    private int stackTop;
    private int stackPointer;
    private int programCounter;
    private int indexRegister;
    private int highestProgramMemoryAddress;
    private SystemState systemState;

    public Chip8System()
    {
        initialiseSystem();
        prepareStateProvider();
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
                if (address <0)
                    throw new IllegalArgumentException(String.format("read memory:out of bounds address: cannot read from address %d",address));
                if (address > AVAILABLE_RAM)
                {
                    // if it is sythetic address, we need to read from the digit pattern table
                    if ((address & 0xFFFF0000) == 0xDEAD0000)
                        return DIGIT_DISPLAY_PATTERNS [address & 0xff];
                    throw new IllegalArgumentException(String.format("read memory:out of bounds address: cannot read from address %d",address));
                }
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
                // TODO:: implement vram;
                return false;
            }

            @Override
            public void eraseDisplay()
            {
                // todo:: implement display and vram clearn
            }

            @Override
            public byte getKey()
            {
                // todo:: return the code of the key - if any is pressed
                return 0;
            }

            @Override
            public byte getTimer()
            {
                // TODO:: get current timer valie
                return 0;
            }

            @Override
            public void setTimer(byte value)
            {
                // todo:: implement timer
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
        };
    }

    private void initialiseSystem()
    {
        final int numberOfRamPages = AVAILABLE_RAM / RAM_PAGE_SIZE;
        Arrays.fill(ram, (byte) 0);
        videoRamBaseAddress = (numberOfRamPages - 1) * RAM_PAGE_SIZE;
        registersBaseAddress = videoRamBaseAddress - 16;
        stackTop = (numberOfRamPages - 2) * RAM_PAGE_SIZE + 0xCF;
        stackPointer = stackTop;
        programCounter = PROGRAM_START;
        highestProgramMemoryAddress = (numberOfRamPages - 2) * RAM_PAGE_SIZE + 0xA0;
        System.out.println(String.format("initialised: total RAM %d bytes, VideoRAM@%04X ,registers@%04X ,Stack@%04X ,PC=%04X , %d bytes available for code",
                AVAILABLE_RAM,videoRamBaseAddress,registersBaseAddress,stackPointer,programCounter,(highestProgramMemoryAddress-programCounter)));
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

    // Debugging helpers
    public void singleStep()
    {
        createInputScanner();
        //inputScanner.useLocale(LOCALE);
        System.out.print("(BREAK)>");
        inputScanner.nextLine();

    }
    private static Scanner inputScanner = null;
    private void createInputScanner()
    {
        if (inputScanner  == null)
            inputScanner = new Scanner(new java.io.BufferedInputStream(System.in), "UTF-8");
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

}
