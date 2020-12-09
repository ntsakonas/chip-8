package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.decompiler.instructions.OpcodeUtil;

public class Goto extends BaseChipInstruction {

    public Goto(int address, byte lsb, byte msb, boolean usesV0) {
        super(address, lsb, msb, String.format("Goto %04X%S", OpcodeUtil.addressFrom(lsb, msb), usesV0 ? " + V0" : ""));
    }
}
