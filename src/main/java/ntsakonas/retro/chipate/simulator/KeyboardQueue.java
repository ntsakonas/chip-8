package ntsakonas.retro.chipate.simulator;

public interface KeyboardQueue {

    byte waitForKey();

    boolean isKeyPressed(int keyCode);
}
