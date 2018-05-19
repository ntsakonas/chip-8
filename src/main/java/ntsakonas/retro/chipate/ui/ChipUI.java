package ntsakonas.retro.chipate.ui;

import ntsakonas.retro.chipate.SystemDisplay;
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

public class ChipUI {


    SystemDisplay systemDisplay =  new SystemDisplay(){

        @Override
        public void refresh(byte[] vram)
        {

        }
    };
    SystemDisplay systemDisplay2 =  new SystemDisplay()
    {
        final int SCREEN_SIZE_X = 64;
        final int SCREEN_SIZE_Y = 32;
        final int SCALE = 3;
        @Override
        public void refresh(byte[] vram)
        {
//            System.out.println("---refresh---");
            final int displaySizeX = SCREEN_SIZE_X * SCALE;
            final int displaySizeY = SCREEN_SIZE_Y * SCALE;
            final int displayYBias = 50;
            BufferedImage vramImage = new BufferedImage(displaySizeX,displaySizeY+displayYBias,BufferedImage.TYPE_INT_RGB);
            for (int y=0;y<SCREEN_SIZE_Y;y++)
            {
                for (int x=0;x<SCREEN_SIZE_X/8;x++)
                {
                   int videoByte = getVideoByte(vram, y, x);
                   int mask = 0x80;
                   for (int bits=0;bits<8;bits++)
                   {
                       boolean isBitOn = ((videoByte & mask) == mask);
                       int ypos = y * SCALE + displayYBias;
                       int xpos = (x * 8 + bits) * SCALE;
                       if (SCALE == 1)
                       {
                           vramImage.setRGB(xpos, ypos, isBitOn ? 0xffffff : 0x000000);
                       }else
                       {
                           for (int yRepeat = 0;yRepeat<SCALE;++yRepeat)
                               for (int xRepeat = 0;xRepeat<SCALE;++xRepeat)
                                   vramImage.setRGB(xpos+xRepeat, ypos+yRepeat, isBitOn ? 0xffffff : 0x000000);
                       }
                       mask >>= 1;
                   }
                }
            }
            display.getGraphics().drawImage(vramImage, 0, 0, null);
            display.updateUI();

        }

        private int getVideoByte(byte[] vram, int y, int x)
        {
            return Byte.toUnsignedInt(vram[y * 8 + x]);
        }
    };

    private JFrame topLevelFrame;
    private JPanel display;
    private static Simulator simulator;


    public ChipUI()
    {
        createAndShowGUI();
    }

    public SystemDisplay getSystemDisplay()
    {
        return systemDisplay2;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        topLevelFrame = new JFrame("Chip-8 Emulator");
        topLevelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topLevelFrame.setBackground(new Color(100, 213, 50));
        topLevelFrame.setPreferredSize(new Dimension(400, 380));

        /*
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(new Color(154, 165, 127));
        menuBar.setPreferredSize(new Dimension(400, 20));
*/
        /*
        //Create a yellow label to put in the content pane.
        JLabel yellowLabel = new JLabel();
        yellowLabel.setOpaque(true);
        yellowLabel.setBackground(new Color(248, 213, 131));
        yellowLabel.setPreferredSize(new Dimension(600, 600));
        */

        display = new JPanel(false);
        display.setPreferredSize(new Dimension(64, 32));
        display.setBackground(new Color(0, 0, 0));


        //Set the menu bar and add the label to the content pane.
        //topLevelFrame.setJMenuBar(menuBar);
        topLevelFrame.getContentPane().add(display, BorderLayout.CENTER);

        //frame.getContentPane().add(yellowLabel, BorderLayout.CENTER);
        //Display the window.
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

        //topLevelFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topLevelFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        topLevelFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                simulator.terminate();
                topLevelFrame.dispose();
                System.out.println("exit");

            }
        });
        topLevelFrame.pack();
        topLevelFrame.setVisible(true);

        //read.setRGB();
    }

    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    public static void main(String[] args) throws IOException
    {

        if (args.length == 0)
        {
            System.out.println("--- Chip-8 simulator by Nick Tsakonas (c) 2018");
            System.out.println("--- usage simulator input.rom [base]");
            System.out.println("          input.rom  - the rom to execute (located at 0x0200)");
            return;
        }
        final byte[] romBytes = Files.readAllBytes(Paths.get(args[0]));

        javax.swing.SwingUtilities.invokeLater(() -> {
            ChipUI chipUI = new ChipUI();
            simulator = new Simulator(new Keyboard(),chipUI.getSystemDisplay());
            simulator.run(romBytes);
        });
//        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI(new File("/home/ntsakonas/Data/Projects/intelij/chip-8/image.jpg")));
    }

}

/*
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChipUI
{

//    http://www.baeldung.com/java-images
//    https://stackoverflow.com/questions/5281262/how-to-close-the-window-in-awt
//    https://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/
//    https://beginnersbook.com/2015/06/java-awt-tutorial/
//
//    https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/


    ChipUI()
    {
        Frame f = new Frame();
        Button b=new Button("click me");
        b.setBounds(30,100,80,30);// setting button position
        f.add(b);//adding button into frame
        f.setSize(300,300);//frame size 300 width and 300 height
        f.setLayout(null);//no layout manager
        f.setVisible(true);//now frame will be visible, by default not visible
        f.setTitle("Chip-8 Simulator");
        f.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                f.dispose();
            }
        });
    }

    public static void main(String[] args)
    {
        ChipUI ui = new ChipUI();
    }
}
*/