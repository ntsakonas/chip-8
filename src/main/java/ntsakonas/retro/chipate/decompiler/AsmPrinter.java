package ntsakonas.retro.chipate.decompiler;

import ntsakonas.retro.chipate.decompiler.instructions.ChipInstruction;

interface AsmPrinter {

    void print(ChipInstruction nextInstruction);
}
