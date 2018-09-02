package ntsakonas.retro.chipate.simulator;

import ntsakonas.retro.chipate.simulator.keymaps.ConvenientKeyMap;
import ntsakonas.retro.chipate.simulator.keymaps.KeyboardMapping;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class Keyboard implements KeyboardInput , KeyboardQueue
{

    // default keyboard mapping
    // Chip8 key -> PC key
    // 0 - 9     -> 0 - 9
    // A         -> A
    // B         -> S
    // C         -> D
    // D         -> Z
    // E         -> X
    // F         -> C
    private KeyboardMapping keyboardMapping;
    // bitmapped value to hold key status
    // maps the keys from lsb -> msb (1->F)
    // a bit set means the key is pressed
    private int keyboardStatus = 0;
    private LinkedBlockingQueue<Integer> keyboardBuffer = new LinkedBlockingQueue<>();

    @Override
    public void keyPressed(char key)
    {
        int keyIndex = keyboardMapping.mapKey(key);
        if (keyIndex != -1)
        {
            keyboardStatus |= (0x1 << keyIndex);
            enqueueKey();
        }
    }

    @Override
    public void keyReleased(char key)
    {
        int keyIndex = keyboardMapping.mapKey(key);
        if (keyIndex != -1)
        {
            keyboardStatus &= ~(0x1 << keyIndex);
            enqueueKey();
        }
    }

    private void enqueueKey()
    {
        keyboardExecutor.submit(() -> keyboardBuffer.offer(keyboardStatus));
    }


    private ScheduledExecutorService keyboardExecutor = Executors.newSingleThreadScheduledExecutor();;


    public Keyboard()
    {
        // using default mapping
        this(new ConvenientKeyMap());
    }

    public Keyboard(KeyboardMapping keyboardMapping)
    {
        this.keyboardMapping = keyboardMapping;
    }

    @Override
    public byte waitForKey()
    {
        // we have to ignore existing keystrokes
        keyboardBuffer.clear();
        try
        {
            System.out.println("waitForKey() enter");
            // wait until ONLY one key press is detected
            Integer key = keyboardBuffer.take();
            //System.out.println("waitForKey() got 1 -> "+key);
            while (key == 0 || Integer.bitCount(key) > 1)
            {
                //System.out.println("waitForKey() got 1 not good.repeating ");
                key = keyboardBuffer.take();
            }
            // one single-key press was detected, the index of the key
            // is the bit number that is set (needs normalisation for 0-indexing)
            int lowestOneBit = Integer.lowestOneBit(key);
            // wait until the key is released before returning
            //System.out.println("waitForKey() got sth good.->"+key+","+Integer.toBinaryString(key)+" , "+lowestOneBit);
            key = keyboardBuffer.take();
            //System.out.println("waitForKey() got 2 ->"+key+","+Integer.toBinaryString(key)+" , "+Integer.lowestOneBit(key));
            while (key != 0 || Integer.bitCount(key) > 1 /*|| Integer.lowestOneBit(key) != lowestOneBit*/)
            {
                //System.out.println("waitForKey() got 2 not good.repeating");
                key = keyboardBuffer.take();
            }
            //System.out.println("waitForKey() done ->"+lowestOneBit);
            //return the index of the first bit set from the right
            int keyIndex = 0x1;
            for (int bitIndex=0; bitIndex<16; bitIndex++)
            {
                if ((lowestOneBit & (keyIndex << bitIndex)) != 0)
                    return (byte)bitIndex;
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        // TODO:: what is a safe value in case of error?
        return 0;
    }

    @Override
    public boolean isKeyPressed(int keyCode)
    {
        return (keyboardStatus & (0x1 << keyCode)) != 0;
    }
}
