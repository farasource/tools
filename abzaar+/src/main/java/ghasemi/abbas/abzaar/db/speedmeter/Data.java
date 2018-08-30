package ghasemi.abbas.abzaar.db.speedmeter;

/**
 * Created by fly on 17/04/15.
 */
public class Data {
    private boolean isRunning;
    private long time;
    private long timeStopped;
    private boolean isFirstTime;

    private double distanceM;
    private double curSpeed;
    private double maxSpeed;

    private OnGpsServiceUpdate onGpsServiceUpdate;

    public interface OnGpsServiceUpdate{
        void update();
    }

    void setOnGpsServiceUpdate(OnGpsServiceUpdate onGpsServiceUpdate){
        this.onGpsServiceUpdate = onGpsServiceUpdate;
    }

    public void update(){
        onGpsServiceUpdate.update();
    }

    public Data() {
        isRunning = false;
        distanceM = 0;
        curSpeed = 0;
        maxSpeed = 0;
        timeStopped = 0;
    }

    public Data(OnGpsServiceUpdate onGpsServiceUpdate){
        this();
        setOnGpsServiceUpdate(onGpsServiceUpdate);
    }

    void addDistance(double distance){
        distanceM = distanceM + distance;
    }

    public double getDistance(){
        return distanceM;
    }

    double getMaxSpeed() {
        return maxSpeed;
    }

    double getAverageSpeed(){
        double average;
        if (time <= 0) {
            average = 0.0;
        } else {
            average = (distanceM / (time / 1000.0)) * 3.6;
        }
        return average;
    }

    double getAverageSpeedMotion(){
        long motionTime = time - timeStopped;
        double average;
        if (motionTime <= 0){
            average = 0.0;
        } else {
            average = (distanceM / (motionTime / 1000.0)) * 3.6;
        }
        return average;
    }

    void setCurSpeed(double curSpeed) {
        this.curSpeed = curSpeed;
        if (curSpeed > maxSpeed){
            maxSpeed = curSpeed;
        }
    }

    boolean isFirstTime() {
        return isFirstTime;
    }

    void setFirstTime(boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
    }

    boolean isRunning() {
        return isRunning;
    }

    void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    void setTimeStopped(long timeStopped) {
        this.timeStopped += timeStopped;
    }

    double getCurSpeed() {
        return curSpeed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

