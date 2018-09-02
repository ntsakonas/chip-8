package ntsakonas.retro.chipate.ui;

import ntsakonas.retro.chipate.SystemDisplay;
import ntsakonas.retro.chipate.debugger.CommandLineDebugger;
import ntsakonas.retro.chipate.simulator.Keyboard;
import ntsakonas.retro.chipate.simulator.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class SharedVideoRam
{
    public byte[] videoRam;
}

public class ChipUI {
    SharedVideoRam sharedVideoRam = new SharedVideoRam();

    SystemDisplay systemDisplay = vram -> sharedVideoRam.videoRam = Arrays.copyOf(vram, vram.length);

    private final int SIMULATOR_WINDOW_WIDTH = 400;
    private final int SIMULATOR_WINDOW_HEIGHT = 380;

    private JFrame topLevelFrame;
    private static Simulator simulator;


    public ChipUI()
    {
    }

    public SystemDisplay getSystemDisplay()
    {
        return systemDisplay;
    }
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        //JFrame.setDefaultLookAndFeelDecorated(true);
        topLevelFrame = new JFrame("Chip-8 Emulator");
        topLevelFrame.setResizable(false);
        topLevelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topLevelFrame.setBackground(new Color(128, 128, 128));
        topLevelFrame.setPreferredSize(new Dimension(SIMULATOR_WINDOW_WIDTH, SIMULATOR_WINDOW_HEIGHT));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        topLevelFrame.setLocation(screenSize.width / 2 - SIMULATOR_WINDOW_WIDTH / 2, screenSize.height / 2 - SIMULATOR_WINDOW_HEIGHT /2 );

        //topLevelFrame.getContentPane().add(new SimulatorDisplay(sharedVideoRam), BorderLayout.CENTER);
        topLevelFrame.add(new SimulatorDisplay(sharedVideoRam), BorderLayout.CENTER);
        topLevelFrame.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent keyEvent)
            {
                System.out.println("key typed:"+keyEvent.getKeyChar());
            }

            @Override
            public void keyPressed(KeyEvent keyEvent)
            {
                System.out.println("key pressed:"+keyEvent.getKeyChar());
            }

            @Override
            public void keyReleased(KeyEvent keyEvent)
            {
                System.out.println("key released:"+keyEvent.getKeyChar());
            }
        });

        topLevelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        topLevelFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                simulator.terminate();
                topLevelFrame.dispose();
                System.out.println("exit");
                System.exit(0);

            }
        });
        topLevelFrame.pack();
        topLevelFrame.setVisible(true);
    }



    public  void startSimulatorUI(String romPath) throws IOException
    {
        final byte[] romBytes = Files.readAllBytes(Paths.get(romPath));
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            simulator = new Simulator(new Keyboard(),getSystemDisplay());
            // TESTING THE DEBUGGER
            simulator.attachDebugger(new CommandLineDebugger());
            simulator.run(romBytes);
        });
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
        ChipUI ui = new ChipUI();
        ui.startSimulatorUI(args[0]);
    }

}

class SimulatorDisplay extends  JPanel
{
    private final int SCREEN_WIDTH_PX = 64;
    private final int SCREEN_HEIGHT_PX = 32;
    final int SCALE = 5;
    final int displaySizeX = SCREEN_WIDTH_PX * SCALE;
    final int displaySizeY = SCREEN_HEIGHT_PX * SCALE;
    final int displayYOffset = 0;

    private final SharedVideoRam sharedVideoRam;
    private BufferedImage vramImage;

    public SimulatorDisplay(SharedVideoRam sharedVideoRam)
    {
        this.sharedVideoRam = sharedVideoRam;
        setupDisplay();
    }

    private void setupDisplay()
    {
        setPreferredSize(new Dimension(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX));
        setBackground(new Color(0, 0, 0));
        vramImage = new BufferedImage(displaySizeX, displaySizeY + displayYOffset, BufferedImage.TYPE_INT_RGB);
        startScreenUpdate();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        //super.paintComponent(g);
        g.drawImage(vramImage, 0, 0, this);
        Toolkit.getDefaultToolkit().sync();
    }

    private void startScreenUpdate()
    {
        ScheduledExecutorService rtcExecutor =Executors.newScheduledThreadPool(1);
        rtcExecutor.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                //synchronized (sharedVideoRam)
                {
                    if (sharedVideoRam.videoRam != null && sharedVideoRam.videoRam.length>0)
                        refresh(sharedVideoRam.videoRam);
                }
            }

            public void refresh(byte[] vram)
            {
                for (int y = 0; y < SCREEN_HEIGHT_PX; y++)
                {
                    for (int x = 0; x < SCREEN_WIDTH_PX / 8; x++)
                    {
                        int videoByte = getVideoByte(vram, y, x);
                        int mask = 0x80;
                        for (int bits = 0; bits < 8; bits++)
                        {
                            boolean isBitOn = ((videoByte & mask) == mask);
                            int ypos = y * SCALE + displayYOffset;
                            int xpos = (x * 8 + bits) * SCALE;
                            if (SCALE == 1)
                            {
                                vramImage.setRGB(xpos, ypos, isBitOn ? 0xffffff : 0x000000);
                            } else
                            {
                                for (int yRepeat = 0; yRepeat < SCALE; ++yRepeat)
                                    for (int xRepeat = 0; xRepeat < SCALE; ++xRepeat)
                                        vramImage.setRGB(xpos + xRepeat, ypos + yRepeat, isBitOn ? 0xffffff : 0x000000);
                            }
                            mask >>= 1;
                        }
                    }
                }
                repaint();
            }
            private int getVideoByte(byte[] vram, int y, int x)
            {
                return Byte.toUnsignedInt(vram[y * 8 + x]);
            }


        }, 0L, 5L, TimeUnit.MILLISECONDS);

    }

}

/*
//    http://www.baeldung.com/java-images
//    https://stackoverflow.com/questions/5281262/how-to-close-the-window-in-awt
//    https://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/
//    https://beginnersbook.com/2015/06/java-awt-tutorial/
//
//    https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/
*/