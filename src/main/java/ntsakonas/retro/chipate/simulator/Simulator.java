package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.SystemDisplay;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Simulator
{
    private Chip8System chip8System;
    private ChipInstructionMicrocodeDecoder decoder;
    private ChipInstructionExecutor executor;
    private boolean terminated;

    public Simulator(Keyboard keyboard,SystemDisplay systemDisplay)
    {
        chip8System = new Chip8System(keyboard,systemDisplay);
        decoder = ChipInstructionMicrocodeDecoder.decoder();
        executor = new BaseInstructionSetExecutor();
    }


    public void run(byte[] romBytes)
    {
        chip8System.placeRomInMemory(romBytes);
        try
        {

            // running the simulator on a different thread I can terminate the application
            // but the screen drawing flashes a lot

            ExecutorService rtcExecutor = Executors.newSingleThreadExecutor();//newScheduledThreadPool(1);
            rtcExecutor.submit(() -> startExecution());

            // running this in the main thread blocks the program from terminating
            //startExecution();
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

            // the execution loop is blocking the main thread and the program cannot exit
            sleep();
        }
    }

    private void sleep()
    {
        // TODO:: I need to simulate the timing of the processor
        // CDP1802 COSMAC board used a 1.7609Mhz clock,
        // each CDP1802 machine cycle equals 8 clock cycles,
        // each machine cycle is about 4.54 microSec in duration.
        // it is no known how many instructions were executed per CHIP8 instruction
        // but lets assume 10.. that means each CHIP8 instruction would last 45.4 microSec
        // lets sleep for this time to emulate the original timing
        try
        {
//            Thread.sleep(0L,45400);
            Thread.sleep(1L);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
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
        Simulator simulator = new Simulator(new Keyboard(), vram ->
        {

        });
        simulator.run(romBytes);
    }
}
