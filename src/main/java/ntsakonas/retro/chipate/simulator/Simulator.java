package ntsakonas.retro.chipate.simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Simulator
{
    private Chip8System chip8System;
    private ChipInstructionMicrocodeDecoder decoder;
    private ChipInstructionExecutor executor;
    private boolean terminated;

    public Simulator()
    {
        chip8System = new Chip8System();
        decoder = ChipInstructionMicrocodeDecoder.decoder();
        executor = new BaseInstructionSetExecutor();
    }


    public void run(byte[] romBytes)
    {
        chip8System.placeRomInMemory(romBytes);
        try
        {
            startExecution();
        }catch (Exception e)
        {
            System.out.println(String.format("Error during execution at address %04X",chip8System.systemState().getProgramCounter()));
            e.printStackTrace();
        }
    }

    public void terminate()
    {
        terminated = true;
        chip8System.shutdown();
    }

    private void startExecution()
    {
        int programCounter = chip8System.getProgramCounter();
        int dgbInstructionCounter = 0;
        terminated = false;
        while (!terminated)
        {
            byte instructionLsb = chip8System.systemState().readMemory(programCounter);
            byte instructionMsb = chip8System.systemState().readMemory(programCounter + 1);

            ChipInstructionMicrocode microCode = decoder.decode(instructionLsb, instructionMsb);
            executor.executeCode(microCode, instructionLsb, instructionMsb,chip8System.systemState());
            programCounter = chip8System.getProgramCounter();
            //chip8System.singleStep();
            //dgbInstructionCounter = dgbInstructionCounter % 4;
            //if (dgbInstructionCounter == 0)
            //   chip8System.displayVram();
        }
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0)
        {
            System.out.println("--- Chip-8 simulator by Nick Tsakonas (c) 2018");
            System.out.println("--- usage simulator input.rom [base]");
            System.out.println("          input.rom  - the rom to execute (located at 0x0200)");
            return;
        }
        byte[] romBytes = Files.readAllBytes(Paths.get(args[0]));
        Simulator simulator = new Simulator();
        simulator.run(romBytes);
    }
}
