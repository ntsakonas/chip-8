package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;

public class ReturnFromSubroutine extends BaseChipInstruction {

    public ReturnFromSubroutine(int address, byte lsb, byte msb) {
        super(address, lsb, msb, "return");
    }
}
