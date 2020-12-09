package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.decompiler.instructions.OpcodeUtil;

public class DoSubroutine extends BaseChipInstruction {

    public DoSubroutine(int address, byte lsb, byte msb) {
        super(address, lsb, msb, String.format("CALL %04X", OpcodeUtil.addressFrom(lsb, msb)));
    }
}
