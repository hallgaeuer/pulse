package hasn.pulse;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Beeper {
    private MusicalTime musicalTime;

    private SoundPool soundPool;

    private int click1Id;

    private int click2Id;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> beeperHandle;

    private Context context;

    private boolean running = false;

    public Beeper(Context context, MusicalTime musicalTime) {
        this.context = context;
        this.musicalTime = musicalTime;

        initializeSoundPool();
    }

    private void initializeSoundPool() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        click1Id = soundPool.load(context, R.raw.click_1, 1);
        click2Id = soundPool.load(context, R.raw.click_2, 1);
    }

    public void start() {
        if (isRunning()) {
            stop();
        }

        running = true;

        Runnable beeper = new Runnable() {
            private int tick = 0;

            public void run() {
                if (tick == 0) {
                    playClick1();
                } else {
                    playClick2();
                }

                if (tick >= musicalTime.getTimeSignatureBeats() - 1) {
                    tick = 0;
                } else {
                    tick++;
                }
            }
        };

        beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 0, calculateMicrosecondDelay(), TimeUnit.MICROSECONDS);

    }

    protected long calculateMillisecondDelay() {
        return (long) (60000 / musicalTime.getBpm() / (musicalTime.getTimeSignatureDivision() / 4));
    }

    protected long calculateMicrosecondDelay() {
        return (long) (calculateMillisecondDelay() * 1000);
    }

    public void toggle() {
        if (isRunning()) {
            stop();
        } else {
            start();
        }
    }

    protected void playClick1() {
        soundPool.play(click1Id, 1, 1, 10, 0, 1);
    }

    protected void playClick2() {
        soundPool.play(click2Id, 1, 1, 10, 0, 1);
    }

    public void stop() {
        if (!isRunning()) {
            return;
        }

        running = false;

        beeperHandle.cancel(true);
    }

    public boolean isRunning() {
        return running;
    }
}
