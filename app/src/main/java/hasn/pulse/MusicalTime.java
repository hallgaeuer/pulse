package hasn.pulse;

public class MusicalTime {
    protected float bpm;

    protected Integer timeSignatureBeats = 4;

    protected Integer timeSignatureDivision = 4;

    public MusicalTime() {
        this(120);
    }

    public MusicalTime(float bpm) {
        this.bpm = bpm;
    }

    public float getBpm() {
        return bpm;
    }

    public void setBpm(float bpm) {
        this.bpm = bpm;
    }

    public Integer getTimeSignatureBeats() {
        return timeSignatureBeats;
    }

    public void setTimeSignatureBeats(Integer timeSignatureBeats) {
        this.timeSignatureBeats = timeSignatureBeats;
    }

    public Integer getTimeSignatureDivision() {
        return timeSignatureDivision;
    }

    public void setTimeSignatureDivision(Integer timeSignatureDivision) {
        this.timeSignatureDivision = timeSignatureDivision;
    }
}
