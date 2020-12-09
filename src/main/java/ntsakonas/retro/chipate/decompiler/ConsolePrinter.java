package ntsakonas.retro.chipate.decompiler;

import ntsakonas.retro.chipate.decompiler.instructions.ChipInstruction;

public class ConsolePrinter implements AsmPrinter {

    @Override
    public void print(ChipInstruction instruction) {
        System.out.println(String.format("%04X  %S  %S", instruction.getAddress(), instruction.getOpcodes(), instruction.getMnemonic()));
    }
}
