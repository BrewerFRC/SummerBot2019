package frc.robot;

import edu.wpi.first.networktables.*;

/**
 * A class to incoroporate vision from a limelight.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Vision {
    private boolean hasTarget = false;

    private double drivePower = 0.0, driveSteer = 0.0;

    final private String TABLE = "limelight";
    final double STEER_K = 0.0, DRIVE_K = 0.0, //values to be multipled by
        DESIRED_TARGET_AREA = 13.0, MAX_SPEED = 0.4;

    public Vision() {

    }

    /**
     * A function to return data from limelight
     * 
     * Normal possible entries:
     * tv	Whether the limelight has any valid targets (0 or 1)
     * tx	Horizontal Offset From Crosshair To Target (LL1: -27 degrees to 27 degrees | LL2: -29.8 to 29.8 degrees)
     * ty	Vertical Offset From Crosshair To Target (LL1: -20.5 degrees to 20.5 degrees | LL2: -24.85 to 24.85 degrees)
     * ta	Target Area (0% of image to 100% of image)
     * ts	Skew or rotation (-90 degrees to 0 degrees)
     * tl	The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
     * tshort	Sidelength of shortest side of the fitted bounding box (pixels)
     * tlong	Sidelength of longest side of the fitted bounding box (pixels)
     * thor	Horizontal sidelength of the rough bounding box (0 - 320 pixels)
     * tvert	Vertical sidelength of the rough bounding box (0 - 320 pixels)
     * getpipe	True active pipeline index of the camera (0 .. 9)
     * camtran	Results of a 3D position solution, 6 numbers: Translation (x,y,y) Rotation(pitch,yaw,roll)
     */
    public double getDouble( String entry) {
        return NetworkTableInstance.getDefault().getTable(TABLE).getEntry(entry).getDouble(0);
    }

    private Boolean hasTarget() {
        //private to limit network table calls
        return getDouble("tv") >= 1.0;
    }

    public void update() {
        hasTarget = hasTarget();
        if (hasTarget) {
            double targetArea = getDouble("ta"); //in percentage of image
            double HOffset = getDouble("tx"), VOffset = getDouble("ty");

            driveSteer =  HOffset * STEER_K;
            drivePower = (this.DESIRED_TARGET_AREA - targetArea) *DRIVE_K;
            if (drivePower > this.MAX_SPEED) {
                driveSteer = MAX_SPEED;
            }

            Common.dashNum("Vetrical offset", VOffset);
            Common.dashNum("Horizental offset", HOffset);
            Common.dashNum("targetArea", targetArea);
        
        }
        Common.dashBool("Has vision target", hasTarget);
    }

}