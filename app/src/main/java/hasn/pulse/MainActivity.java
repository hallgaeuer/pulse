package hasn.pulse;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    protected MusicalTime musicalTime;

    protected TextView bpmText;

    protected Button toggleButton;

    protected Beeper beeper;

    protected SeekBar bpmSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicalTime = new MusicalTime();
        beeper = new Beeper(this, musicalTime);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bpmText = (TextView) findViewById(R.id.bpmText);
        toggleButton = (Button) findViewById(R.id.toggleButton);

        initializeBpmSlider();
        initializeTimeSignatureSpinner();

        updateBpm(musicalTime.bpm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void initializeBpmSlider() {
        bpmSlider = (SeekBar) findViewById(R.id.bpmSlider);

        bpmSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            protected boolean beeperWasRunningOnStart = false;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {;
                if (progress >= 1 && fromUser) {
                    updateBpm(progress);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                if (beeper.isRunning()) {
                    beeperWasRunningOnStart = true;

                    beeper.stop();
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (beeperWasRunningOnStart) {
                    beeper.start();
                    beeperWasRunningOnStart = false;
                }
            }
        });
    }

    protected void initializeTimeSignatureSpinner() {
        Spinner timeSignatureBeatsSpinner = (Spinner) findViewById(R.id.timeSignatureBeats);
        Spinner timeSignatureDivisionSpinner = (Spinner) findViewById(R.id.timeSignatureDivision);

        // Initialize Beats Spinner
        Integer[] timeSignatureBeatsSpinnerItems = new Integer[32];

        for (int i = 0; i < timeSignatureBeatsSpinnerItems.length; i++) {
            timeSignatureBeatsSpinnerItems[i] = i + 1;
        }

        ArrayAdapter<Integer> timeSignatureBeatsSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, timeSignatureBeatsSpinnerItems);
        timeSignatureBeatsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSignatureBeatsSpinner.setAdapter(
                timeSignatureBeatsSpinnerAdapter
        );

        // Set default value
        timeSignatureBeatsSpinner.setSelection(
                timeSignatureBeatsSpinnerAdapter.getPosition(musicalTime.getTimeSignatureBeats())
        );

        // Add listener
        timeSignatureBeatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                musicalTime.setTimeSignatureBeats(
                        (Integer) parent.getItemAtPosition(position)
                );

                refreshBeeper();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize Time Signutare Division Spinner
        ArrayAdapter<CharSequence> timeSignatureDivisionSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.time_signature_divisions, android.R.layout.simple_spinner_item);
        timeSignatureDivisionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSignatureDivisionSpinner.setAdapter(
                timeSignatureDivisionSpinnerAdapter
        );

        // Set default value
        timeSignatureDivisionSpinner.setSelection(
                timeSignatureDivisionSpinnerAdapter.getPosition(musicalTime.getTimeSignatureDivision().toString())
        );

        // Add listener
        timeSignatureDivisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                musicalTime.setTimeSignatureDivision(
                        Integer.valueOf((String) parent.getItemAtPosition(position))
                );

                refreshBeeper();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void refreshBpmText() {
        bpmText.setText(new StringBuilder(Float.toString(musicalTime.getBpm())).append(" BPM"));
    }

    public void refreshBpmSlider() {
        bpmSlider.setProgress(Math.round(musicalTime.getBpm()));
    }

    public void incrementBpm(View view) {
        updateBpm(musicalTime.getBpm() + 1);
    }

    public void decrementBpm(View view) {
        updateBpm(musicalTime.getBpm() - 1);
    }

    public void updateBpm(float bpm) {
        musicalTime.setBpm(bpm);

        refreshBpmText();
        refreshBeeper();
        refreshBpmSlider();
    }

    public void refreshBeeper() {
        if (beeper.isRunning()) {
            beeper.start();
        }
    }

    public void toggleMetronome(View view) {
        beeper.toggle();
    }
}
