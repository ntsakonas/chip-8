package ntsakonas.retro.chipate.decompiler.instructions.originalset;

import ntsakonas.retro.chipate.decompiler.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.decompiler.instructions.OpcodeUtil;

public class DoMachineLanguageSubroutine extends BaseChipInstruction {

    public DoMachineLanguageSubroutine(int address, byte lsb, byte msb) {
        super(address, lsb, msb, String.format("CALL ML %04X", OpcodeUtil.addressFrom(lsb, msb)));
    }
}
