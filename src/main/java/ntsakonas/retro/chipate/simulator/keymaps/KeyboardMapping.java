package ntsakonas.retro.chipate.simulator.keymaps;

// maps ascii chars to key indices (0x00-0x0f)
// returns -1 if an invalid/unknown key was requested
public interface KeyboardMapping {

    int mapKey(char key);
}
