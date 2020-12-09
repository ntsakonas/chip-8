package ntsakonas.retro.chipate.decompiler;

import ntsakonas.retro.chipate.instructions.ChipInstruction;
import ntsakonas.retro.chipate.instructions.Instructions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Decompiler {

    void decompile(byte[] romBytes, AsmPrinter printer) {
        int romSize = romBytes.length / 2;
        Instructions.Parser instructionParser = Instructions.parser();
        // all Chip-8 programs start from location 0x200 (512d)
        final int baseAddress = 0x200;
        // this represents the program counter;
        int pc = 0;
        for (int i = 0; i < romSize; i++) {
            ChipInstruction nextInstruction = instructionParser.decode(baseAddress + pc, romBytes[pc], romBytes[pc + 1]);
            printer.print(nextInstruction);
            pc += 2;
        }
    }

    //
    // for stand alone usage
    //
    public static void main(String[] args) throws IOException {
        boolean inputIsBinary = true;
        if (args.length == 0)
            throw new IllegalArgumentException("Chip-8 decompiler: please provide the file to decompile.");
        if (args.length == 2)
            inputIsBinary = Boolean.parseBoolean(args[1]);
        if (!inputIsBinary)
            throw new IllegalArgumentException("At the moment only binary ROM files are supported");
        byte[] romBytes = Files.readAllBytes(Paths.get(args[0]));

        System.out.println("--- Chip-8 decompiler by Nick Tsakonas (c) 2018");
        System.out.println("--- asm listing of rom [" + args[0] + "]");
        System.out.println();
        System.out.println("ADDR  OPCD  MNEMONIC");
        System.out.println("-----------------------------------------------");
        Decompiler decompiler = new Decompiler();
        decompiler.decompile(romBytes, new ConsolePrinter());
        System.out.println();
        System.out.println("--- asm listing done!");

    }
}
