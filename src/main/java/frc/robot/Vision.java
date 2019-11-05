package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.*;
import edu.wpi.first.wpilibj.SerialPort.StopBits;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * A class to incoroporate vision from a limelight.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 */
public class Vision {
    public enum States {
        OFF,
        START,
        APPROACH,
        AT_TARGET,
        BACK_UP,
        STOP;
    }
    private long saveTime;

    private States state = States.OFF;

    private boolean hasTarget = false;

    private double drivePower = 0.0, driveSteer = 0.0;

    final private String TABLE = "limelight";

    private static NetworkTableInstance table = null;

    final double STEER_K = 0.0, DRIVE_K = 0.0, //values to be multipled by
        DESIRED_TARGET_AREA = 13.0, MAX_SPEED = 0.4;
    
    private DriveTrain dt;
    private Arm arm;
    private Intake intake;

    public Vision(DriveTrain dt, Arm arm, Intake intake) {
        this.dt = dt;
        this.arm = arm;
        this.intake = intake;
    }
    
	public static enum LIGHTMODE {
		eOn, eOff, eBlink
	}

	public static enum CAMERAMODE{
		eVision, eDriver
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

    public void setLimelight(boolean OnOff) {
        //set limelight on or off
    }

    //determines the distiance of the target and moves towords target using drive.
    //returns True if at Vision Target.
    public boolean approachTarget() {
        boolean atTarget = false;
        if (hasTarget) {
            double targetArea = getDouble("ta"); //in percentage of image
            double HOffset = getDouble("tx");
            //double VOffset = getDouble("ty");

            driveSteer =  HOffset * STEER_K;
            drivePower = (this.DESIRED_TARGET_AREA - targetArea) *DRIVE_K;
            if (drivePower > this.MAX_SPEED) {
                driveSteer = MAX_SPEED;
            }
            if (targetArea >= DESIRED_TARGET_AREA) {
                atTarget = true;
            } else {
                atTarget = false;
            }

            //Common.dashNum("Vetrical offset", VOffset);
            Common.dashNum("Horizental offset", HOffset);
            Common.dashNum("targetArea", targetArea);
        } else {
            driveSteer = 0;
            drivePower = 0;
        }
        dt.arcadeDrive(drivePower, driveSteer);
        return atTarget;
    }

    // Sets vision to STOP if not already OFF
    public void stop() {
        if (state != States.OFF) {
            state = States.STOP;
        }
    }

    // Either starts vision track, or keeps it running
    public void go() {
       if (state == States.OFF && intake.hasGamePiece() == false) {
           state = States.START;
       } 
    }

    public void update() {
        hasTarget = hasTarget();
        switch(state) {
        case OFF:
            //CAMERAMODE = eDriver;
            setLimelight(false);
            break;
        case START:
            //CAMERAMODE = eVision;
            setLimelight(true);
            arm.setTarget(90);
            intake.pnuClosed();
            state = States.APPROACH;
            break;
        case APPROACH:
            //Drive toward target
            if (approachTarget() == true) {
                state = States.AT_TARGET;
            }
            break;
        case AT_TARGET:
            if (intake.hasGamePiece() == true) {
                saveTime = Common.time();
                state = States.BACK_UP;
            } else {
                intake.intakeHatch();
            }
            break;
        case BACK_UP:
            if (saveTime + 500 >= Common.time()) {
                state = States.STOP;
            } else {
                dt.arcadeDrive(-.3, 0); 
            }
            break;
        case STOP:
            dt.arcadeDrive(0, 0);
            state = States.OFF;
            break;
        }
        Common.dashBool("Has vision target", hasTarget);
    }

}