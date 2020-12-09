package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.SystemDisplay;
import ntsakonas.retro.chipate.debugger.Chip8Debugger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator
{
    private Chip8System chip8System;
    private ChipInstructionMicrocodeDecoder decoder;
    private ChipInstructionExecutor executor;
    private boolean terminated;
    private ExecutorService programExecutionThread;
    // I will probably need more than one observers soon
    // to implement various ideas...let's start with only the debugger
    // and I will extend it soon
    private Chip8System.SystemStateObserver debugger;

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
            System.out.println(String.format("Error during execution at address %04X",chip8System.getSystemState().getProgramCounter()));
            e.printStackTrace();
        }
    }

    public void terminate()
    {
        terminated = true;
        programExecutionThread.shutdown();
        chip8System.shutdown();
    }

    public void attachDebugger(Chip8System.SystemStateObserver debugger)
    {
        this.debugger = debugger;
        debugger.active(true);
    }

    public void detachDebugger()
    {
        debugger.active(false);
        this.debugger = null;
    }

    private void startExecution()
    {
        int programCounter = chip8System.getProgramCounter();
        terminated = false;
        while (!terminated)
        {
            if (debugger != null)
               debugger.observe(chip8System.getSystemState());
            byte instructionLsb = chip8System.getSystemState().readMemory(programCounter);
            byte instructionMsb = chip8System.getSystemState().readMemory(programCounter + 1);

            ChipInstructionMicrocode microCode = decoder.decode(instructionLsb, instructionMsb);
            executor.executeCode(microCode, instructionLsb, instructionMsb,chip8System.getSystemState());
            programCounter = chip8System.getProgramCounter();
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
        // UPDATE: using 1ms the emulations is very fast. using an emulated clock of 500Hz as described here
        // https://jackson-s.me/2019/07/13/Chip-8-Instruction-Scheduling-and-Frequency.html
        // the emulation speed is better
        try
        {
            Thread.sleep(2L);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
