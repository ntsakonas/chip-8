package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;

public class EraseDisplay extends BaseChipInstruction {

    public EraseDisplay(int address, byte lsb, byte msb) {
        super(address, lsb, msb, "Erase display");
    }
}
