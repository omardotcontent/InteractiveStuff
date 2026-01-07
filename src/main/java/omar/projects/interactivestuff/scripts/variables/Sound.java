package omar.projects.interactivestuff.scripts.variables;


import me.abdelaziz.api.annotation.VynConstructor;
import me.abdelaziz.api.annotation.VynFunc;
import me.abdelaziz.api.annotation.VynType;

@VynType(name = "Sound")
public final class Sound {

    private String soundId;
    private double volume;
    private double pitch;
    private Position position;

    @VynConstructor
    public Sound(final String soundId, final double volume, final double pitch, final Position position) {
        this.soundId = soundId;
        this.volume = volume;
        this.pitch = pitch;
        this.position = position;
    }



    @VynFunc
    public Position getPosition() {
        return position;
    }

    @VynFunc
    public String getName() {
        return soundId;
    }

    @VynFunc
    public double getVolume() {
        return volume;
    }

    @VynFunc
    public double getPitch() {
        return pitch;
    }

    @VynFunc
    public void setSoundId(final String soundId) {
        this.soundId = soundId;
    }

    @VynFunc
    public void setVolume(final double volume) {
        this.volume = volume;
    }

    @VynFunc
    public void setPitch(final double pitch) {
        this.pitch = pitch;
    }

    @VynFunc
    public void setPosition(final Position position) {
        this.position = position;
    }

    @VynFunc
    public String toString() {
        return "Sound{name=" + soundId + ", volume=" + volume + ", pitch=" + pitch + ", position=" + position + "}";
    }
}