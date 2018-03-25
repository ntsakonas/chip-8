package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.ConsoleInput;
import ntsakonas.retro.chipate.SystemInput;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// WIP
public class Keyboard
{
    private SystemInput systemInput;
    private ScheduledExecutorService keyboardExecutor;

    private byte keyPressed;

    public Keyboard()
    {
        systemInput = new ConsoleInput();
        /*
        keyboardExecutor = Executors.newSingleThreadScheduledExecutor();
        keyboardExecutor.submit(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (systemInput)
                {
                    systemInput.
                }

            }
        })*/
    }

    public byte getKeyPressed()
    {
        return keyPressed;
    }

    public byte waitForKey()
    {
        return keyPressed;
    }
}
