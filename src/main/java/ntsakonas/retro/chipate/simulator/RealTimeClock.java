package ntsakonas.retro.chipate.simulator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealTimeClock
{
    private ScheduledExecutorService rtcExecutor;
    RealTimeClock()
    {
        rtcExecutor = Executors.newScheduledThreadPool(1);
    }

    void start(Runnable callback)
    {
        rtcExecutor.scheduleAtFixedRate(callback,0L,16666L, TimeUnit.MICROSECONDS);
    }

    void stop()
    {
        if (!rtcExecutor.isTerminated())
            rtcExecutor.shutdownNow();
    }
}
