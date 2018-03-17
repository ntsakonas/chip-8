package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;
import ntsakonas.retro.chipate.instructions.OpcodeUtil;

public class DoMachineLanguageSubroutine  extends BaseChipInstruction
{
    public DoMachineLanguageSubroutine(int address, byte lsb, byte msb)
    {
        super(address,lsb,msb,String.format("CALL ML %04X", OpcodeUtil.addressFrom(lsb,msb)));
    }
}
