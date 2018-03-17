package ntsakonas.retro.chipate.simulator;

import java.util.Arrays;

public class Chip8System
{
    public interface SystemState
    {
        byte getRegister(int registerNumber);
        void setRegister(int registerNumber, byte value);
        byte readMemory(int address);
        void setMemory(int address,byte value);
        void setProgramCounter(int address);
        int getProgramCounter();
    }

    private final int AVAILABLE_RAM = 2048;
    private final int RAM_PAGE_SIZE = 256;
    private final int PROGRAM_START = 0x200;
    private byte[] ram = new byte[AVAILABLE_RAM];
    private int videoRamBaseAddress;
    private int registersBaseAddress;
    private int stackPointer;
    private int programCounter;
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
                if (address <0 || address > AVAILABLE_RAM)
                    throw new IllegalArgumentException(String.format("read memory:out of bounds address: cannot read from address %d",address));
                return ram[address];
            }

            @Override
            public void setMemory(int address, byte value)
            {
                if (address <0 || address > AVAILABLE_RAM)
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
        };
    }

    private void initialiseSystem()
    {
        final int numberOfRamPages = AVAILABLE_RAM / RAM_PAGE_SIZE;
        Arrays.fill(ram, (byte) 0);
        videoRamBaseAddress = (numberOfRamPages - 1) * RAM_PAGE_SIZE;
        registersBaseAddress = videoRamBaseAddress - 16;
        stackPointer = (numberOfRamPages - 2) * RAM_PAGE_SIZE + 0xCF;
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

    public void displayResisters()
    {
        System.out.println("Chip-8 status:");
        System.out.println(String.format("pc: %04X",programCounter));
        for (int i=0;i<4;i++)
        {
            System.out.println(String.format("V%02d = %02X   V%02d = %02X   V%02d = %02X   V%02d = %02X",
                    i*4,systemState.getRegister(i*4),
                    i*4+1,systemState.getRegister(i*4+1),
                    i*4+2,systemState.getRegister(i*4+2),
                    i*4+3,systemState.getRegister(i*4+3)));

        }
        System.out.println("---------------------------------------------");

    }

}
