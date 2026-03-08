package pl.stonedrop.managers;

public class DropSettings {
    private boolean ironEnabled = true;
    private boolean goldEnabled = true;
    private boolean cobbleEnabled = true;

    public boolean isIronEnabled() {
        return ironEnabled;
    }

    public void setIronEnabled(boolean ironEnabled) {
        this.ironEnabled = ironEnabled;
    }

    public boolean isGoldEnabled() {
        return goldEnabled;
    }

    public void setGoldEnabled(boolean goldEnabled) {
        this.goldEnabled = goldEnabled;
    }

    public boolean isCobbleEnabled() {
        return cobbleEnabled;
    }

    public void setCobbleEnabled(boolean cobbleEnabled) {
        this.cobbleEnabled = cobbleEnabled;
    }
}
