package ntsakonas.retro.chipate;

import ntsakonas.retro.chipate.instructions.ChipInstruction;

public class ConsolePrinter implements AsmPrinter
{

    @Override
    public void print(ChipInstruction instruction)
    {
        System.out.println(String.format("%04X  %S  %S",instruction.getAddress(),instruction.getOpcodes(),instruction.getMnemonic()));
    }
}
