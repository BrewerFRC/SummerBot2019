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
    DriveTrain dt;

    private double drivePower = 0.0, driveSteer = 0.0;

    final private String TABLE = "limelight";
    final double STEER_K = 0.1, //values to be multipled by 
        DRIVE_K = 0.075 , //Drive at 40% power when at 6.8 away from target area
        DESIRED_TARGET_AREA = 8.829,
        MAX_SPEED = 0.4, MIN_SPEED = 0.1,
        MAX_TURN = 0.25, MIN_TURN = 0.1;

    public Vision(DriveTrain dt) {
        this.dt = dt;
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
     * tvert	Vertical sidelength of the rough bounding box (0  - 320 pixels)
     * getpipe	True active pipeline index of the camera (0 .. 9)
     * camtran	Results of a 3D position solution, 6 numbers: Translation (x,y,z) Rotation(pitch,yaw,roll)
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
        Common.dashBool("Has Vision Target", hasTarget);
        double targetArea = getDouble("ta"); //in percentage of image
        double HOffset = getDouble("tx"), VOffset = getDouble("ty");
        if (hasTarget) {
            if (Common.between(HOffset, -0.2, 0.2)) {
                driveSteer = 0;
            } else {
                if (driveSteer > 0) {
                    driveSteer =  HOffset * STEER_K;
                    if (driveSteer > this.MAX_TURN) {
                        driveSteer = this.MAX_TURN;
                    } else if (driveSteer < this.MIN_TURN) {
                        driveSteer = this.MIN_TURN;
                    }
                } else if (driveSteer < 0) {
                    driveSteer =  HOffset * STEER_K;
                    if (driveSteer < -this.MAX_TURN) {
                        driveSteer = -this.MAX_TURN;
                    } else if (driveSteer > -this.MIN_TURN) {
                        driveSteer = -this.MIN_TURN;
                    }
                }
            }
            drivePower = (this.DESIRED_TARGET_AREA - targetArea) *DRIVE_K;
            if (drivePower > this.MAX_SPEED) {
                drivePower = MAX_SPEED;
            } else if (drivePower < this.MIN_SPEED) {
                drivePower = MIN_SPEED;
            }
            
        
        } else {
            drivePower = 0;
            driveSteer = 0;

        }
        Common.dashNum("Vertical offset", VOffset);
        Common.dashNum("Horizontal offset", HOffset);
        Common.dashNum("targetArea", targetArea);
        this.dt.arcadeDrive(drivePower, driveSteer);
    }


    public void debug() {
        Common.dashNum("Vision steer", driveSteer);
        Common.dashNum("Vision drive", drivePower);
    }

}