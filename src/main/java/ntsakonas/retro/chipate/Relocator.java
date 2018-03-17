package ntsakonas.retro.chipate;

import ntsakonas.retro.chipate.instructions.ChipInstruction;
import ntsakonas.retro.chipate.instructions.OpcodeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Relocator
{
    public byte[] relocate(byte[] romBytes, int newStart)
    {
        // all (or most) Chip-8 programs start from location 0x200 (512d)
        return relocate(romBytes,newStart,0x200);
    }

    public byte[] relocate(byte[] romBytes, int newStart, int originalStart)
    {
        int romSize = romBytes.length/2;
        if (romSize * 2 != romBytes.length)
            throw new IllegalArgumentException("It looks like the rom is missing some bytes..?");

        byte[] relocatedRomBytes = Arrays.copyOf(romBytes,romBytes.length);
        // this represents the program counter;
        int pc = 0;
        int offset = originalStart - newStart;
        for (int i = 0;i < romSize;i++)
        {
            if (needsRelocation(romBytes[pc], romBytes[pc+1]))
                locate(romBytes, relocatedRomBytes, pc, offset);
            pc += 2;
        }
        return relocatedRomBytes;
    }

    private void locate(byte[] romBytes, byte[] relocatedRomBytes, int pc, int offset)
    {
        int originalAddress = OpcodeUtil.addressFrom(romBytes[pc], romBytes[pc + 1]);
        byte[] newAddress = OpcodeUtil.addressToBytes(originalAddress - offset);
        relocatedRomBytes[pc] = (byte)((romBytes[pc] & 0xf0) | (newAddress[0] & 0x0f));
        relocatedRomBytes[pc+1] = newAddress[1];
    }

    private boolean needsRelocation(byte lsbByte, byte msbByte)
    {
        int opcodeClass = OpcodeUtil.nibbles(lsbByte)[0];
        boolean isReturnFromSubroutine = (lsbByte == 0x00 && Byte.toUnsignedInt(msbByte) == 0xEE);
        boolean isEraseDisplay = (lsbByte == 0x00 && Byte.toUnsignedInt(msbByte) == 0xE0);
        if (isEraseDisplay || isReturnFromSubroutine)
            return false;
        return opcodeClass == 0 || opcodeClass == 1 || opcodeClass == 2
                                                     || opcodeClass == 0x0A || opcodeClass == 0x0B;
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0)
        {
            System.out.println("--- Chip-8 relocator by Nick Tsakonas (c) 2018");
            System.out.println("--- usage relocator input.rom output.rom [base]");
            System.out.println("          input.rom  - the original rom to relocate (located at 0x200)");
            System.out.println("          output.rom - the name of the relocated rom");
            System.out.println("          base - the new start address for the rom");
            return;
        }
        byte[] romBytes = Files.readAllBytes(Paths.get(args[0]));

        Relocator relocator = new Relocator();
        byte[] relocatedRomBytes = relocator.relocate(romBytes,0x0000);
        Files.write(Paths.get(args[1]),relocatedRomBytes);
    }
}
