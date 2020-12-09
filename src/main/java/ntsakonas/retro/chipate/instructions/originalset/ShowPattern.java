package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.instructions.OpcodeUtil;

public class ShowPattern extends BaseChipInstruction {

    public ShowPattern(int address, byte lsb, byte msb) {
        super(address, lsb, msb, String.format("Show %dMI@V%dV%d", OpcodeUtil.nibbles(msb)[1], OpcodeUtil.nibbles(lsb)[1], OpcodeUtil.nibbles(msb)[0]));
    }
}
