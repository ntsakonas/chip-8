package ntsakonas.retro.chipate;

import ntsakonas.retro.chipate.instructions.ChipInstruction;

interface AsmPrinter {

    void print(ChipInstruction nextInstruction);
}
