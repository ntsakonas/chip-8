package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;

public class ReturnFromSubroutine extends BaseChipInstruction {

    public ReturnFromSubroutine(int address, byte lsb, byte msb) {
        super(address, lsb, msb, "return");
    }
}
