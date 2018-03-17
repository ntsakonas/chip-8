package ntsakonas.retro.chipate.instructions.originalset;

import ntsakonas.retro.chipate.instructions.BaseChipInstruction;

public class EraseDisplay extends BaseChipInstruction
{
    public EraseDisplay(int address, byte lsb, byte msb)
    {
        super(address,lsb,msb,"Erase display");
    }
}
