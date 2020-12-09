package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.decompiler.instructions.OpcodeUtil;

public class ShowPattern extends BaseChipInstruction {

    public ShowPattern(int address, byte lsb, byte msb) {
        super(address, lsb, msb, String.format("Show %dMI@V%dV%d", OpcodeUtil.nibbles(msb)[1], OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]));
    }
}
