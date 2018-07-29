package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.SystemDisplay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator
{
    private Chip8System chip8System;
    private ChipInstructionMicrocodeDecoder decoder;
    private ChipInstructionExecutor executor;
    private boolean terminated;
    private ExecutorService programExecutionThread;

    public Simulator(Keyboard keyboard,SystemDisplay systemDisplay)
    {
        chip8System = new Chip8System(keyboard,systemDisplay);
        decoder = ChipInstructionMicrocodeDecoder.decoder();
        executor = new BaseInstructionSetExecutor();
        programExecutionThread = Executors.newSingleThreadExecutor();

    }

    public void run(byte[] romBytes)
    {
        chip8System.placeRomInMemory(romBytes);
        try
        {
            programExecutionThread.submit(() -> startExecution());
        }catch (Exception e)
        {
            System.out.println(String.format("Error during execution at address %04X",chip8System.systemState().getProgramCounter()));
            e.printStackTrace();
        }
    }

    public void terminate()
    {
        terminated = true;
        programExecutionThread.shutdown();
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
            simulateRealSystemTiming();
        }
    }

    private void simulateRealSystemTiming()
    {
        // We need to simulate the timing of the processor so that the speed of execution
        // of the programs almost matches the one we would get on the  original board.
        // CDP1802 COSMAC board used a 1.7609Mhz clock,
        // each CDP1802 machine cycle equals 8 clock cycles,
        // each machine cycle is about 4.54 microSec in duration.
        // it is no known how many instructions were executed per CHIP8 instruction
        // but lets assume 20.. that means each CHIP8 instruction would last 90.8 microSec
        // lets sleep for this time to emulate the original timing
        // NOTE: the minimum delays obtainable in Java is 1 msec, which is fine.
        try
        {
            Thread.sleep(1L);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
