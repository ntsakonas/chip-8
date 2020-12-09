package ntsakonas.retro.chipate.decompiler;

import ntsakonas.retro.chipate.instructions.ChipInstruction;

interface AsmPrinter {

    void print(ChipInstruction nextInstruction);
}
