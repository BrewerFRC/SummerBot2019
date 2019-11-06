package frc.robot;

import edu.wpi.first.networktables.*;

/**
 * A class to incoroporate vision from a limelight.
 * 
 * @author Brewer FIRST Robotics Team 4564
 * @author Brent Roberts
 * @author Samuel Woodward
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
    DriveTrain dt;
    Arm arm;
    Intake intake;

    private double drivePower = 0.0, driveSteer = 0.0;

    final private String TABLE = "limelight";
    final double STEER_K = 0.1, //Was 0.12 values to be multipled by 
        DRIVE_K = 0.125 , //Was .15 Drive at 40% power when at 6.8 away from target area
        DESIRED_TARGET_AREA = 6.8
        ,
        MAX_SPEED = 0.6, MIN_SPEED = 0.2,
        MAX_TURN = 0.5, MIN_TURN = 0.1; //max was .4

    public Vision(DriveTrain dt, Arm arm, Intake intake) {
        this.dt = dt;
        this.arm = arm;
        this.intake = intake;
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

    private void setControl(String entry, double  value) {
        NetworkTableInstance.getDefault().getTable(TABLE).getEntry(entry).setNumber(value);
    }

    public void setLimelight(boolean On) {
        //set limelight on or off
        if (On) {
            setControl("ledMode", 0); //on
        } else {
            setControl("ledMode", 1);
        }
    }

    public boolean approachTarget() {
        boolean atTarget = false;
        Common.dashBool("Has Vision Target", hasTarget);
        double targetArea = getDouble("ta"); //in percentage of image
        double HOffset = getDouble("tx"), VOffset = getDouble("ty");
        if (hasTarget) {
            if (Common.between(HOffset, -0.2, 0.2)) {
                driveSteer = 0;
            } else {
                if (HOffset > 0) {
                    driveSteer =  HOffset * STEER_K;
                    if (driveSteer > this.MAX_TURN) {
                        driveSteer = this.MAX_TURN;
                    } else if (driveSteer < this.MIN_TURN) {
                        driveSteer = this.MIN_TURN;
                    }
                } else if (HOffset < 0) {
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
            if (targetArea >= DESIRED_TARGET_AREA) {
                atTarget = true;
            } else {
                atTarget = false;
            }
            
        
        } else {
            drivePower = 0;
            driveSteer = 0;

        }
        Common.dashNum("Vertical offset", VOffset);
        Common.dashNum("Horizontal offset", HOffset);
        Common.dashNum("targetArea", targetArea);
        this.dt.arcadeDrive(drivePower, driveSteer);
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
            if (saveTime + 500 <= Common.time()) {
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


    public void debug() {
        Common.dashNum("Vision steer", driveSteer);
        Common.dashNum("Vision drive", drivePower);
        Common.dashStr("Current State", state.toString());
    }

}