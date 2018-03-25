package ntsakonas.retro.chipate.ui;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChipUI
{
    /*
    http://www.baeldung.com/java-images
    https://stackoverflow.com/questions/5281262/how-to-close-the-window-in-awt
    https://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/
    https://beginnersbook.com/2015/06/java-awt-tutorial/

    https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/
     */

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
