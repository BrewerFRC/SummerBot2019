package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.*;

public class Intake {
    private static final Spark intakeMot = new Spark(Constants.INTAKE);
    public static final DigitalInput intakeLimit = new DigitalInput(Constants.INTAKE_LIMIT);

    private final double  P_BALL_INTAKE = -1.0, P_HATCH_INTAKE = -1.0,
    P_BALL_SHOOT = 0.6, P_HATCH_PLACE = 1.0;
    public static final double BALL_LOADED_VOLT = 2;
    private Solenoid armClosed;
    private Solenoid armOpen;
    private Long saveTime;

    private AnalogInput irInput;
    private static final int
        IDLE = 0,
        HATCH_INTAKE = 1, //intake wheels until limit switch triggered pnu closed
        BALL_INTAKE = 2, //intake wheels until ir sensor triggered pnu open
        HAS_HATCH = 3,
        HAS_BALL = 4,
        HATCH_PLACE = 5, //wheels out till time or limit switch pnu closed
        BALL_PLACE = 6; //spin wheels out till time or ir sensor pnu open

    private int state = IDLE;

    
    private double previousReading = 0;

    public Intake() {
        irInput = new AnalogInput(Constants.IR_PORT);
        
        intakeMot.setInverted(true);

        armOpen = new Solenoid(Constants.ARM_PNU_OPEN);
        armClosed = new Solenoid(Constants.ARM_PNU_CLOSE);
    }
    public double getDistance(){
        double out = 23.1186 * Math.pow(0.999014, irInput.getVoltage());

        return out;
    }
    public boolean hasBall() {
        if(irInput.getVoltage() >= BALL_LOADED_VOLT) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean hasHatch() {
        return intakeLimit.get();
    }
    
    public void intakeHatch() {
        if (!hasGamePiece()) {
            //pnuClose();
            //setPower(this.P_HATCH_INTAKE);//Should pull in
            state = HATCH_INTAKE;
        }
    }

    public void stopIntake() {
        if (!hasGamePiece()) {
            //pnuClose();
            //setPower(this.P_HATCH_INTAKE);//Should pull in
            state = IDLE;
        }
    }

    public void intakeBall() {
        if (!hasGamePiece()) {
            //pnuOpen();
            state = BALL_INTAKE;
        }
    }

    public void placeHatch() {
        state = HATCH_PLACE;
        saveTime = Common.time();
        //setPower(this.P_HATCH_PLACE);
    }

    public void ejectBall() {
        state = BALL_PLACE;
        saveTime = Common.time();
    }

    public void cubeEject() {
        ejectBall();
    }

    public void placeGamePiece() {
        if (state==HAS_HATCH) {
            placeHatch();
        } else if (state==HAS_BALL) {
            ejectBall();
        }
    }

    public void update() {
        switch (state) {
        case IDLE:
            if (hasBall()) {
                state = HAS_BALL;
            } else if (hasHatch()) {
                state = HAS_HATCH;
            } else {
                setPower(0.0);
            }
            
            break;
        case HATCH_INTAKE:
            pnuClosed();
            setPower(1.0);
            if (hasHatch()) {
                state = HAS_HATCH;
            }
            break;
        case BALL_INTAKE:
            pnuOpen();
            setPower(-1.0);
            if (hasBall()) {
                state = HAS_BALL;
            }
            break;
        case HAS_BALL:
            //Not sure about this
            if (!hasBall()) {
                state = IDLE;
            } else if (hasBall()){
                setPower(-0.15);
            }
            break;
        case HAS_HATCH:
            //if (!hasHatch()) {
            //    state =  IDLE;
            //} else {
            setPower(0.25);
            //}
            break;
        case HATCH_PLACE:
            setPower(this.P_HATCH_PLACE);
            if (Common.time() > saveTime + 500) {
                state =  IDLE;
            }  else {
                setPower(-.75);
            }
            break;
        case BALL_PLACE:
            setPower(this.P_BALL_SHOOT);
            if (!hasBall()) {
                state =  IDLE;
                setPower(0.75);
            }
            break;
        }
    }

    public boolean hasGamePiece() {
        return hasBall() || hasHatch();
    }

    private void pnuClosed() {
        armOpen.set(true);
        armClosed.set(false);
    }


    private void pnuOpen() {
        armOpen.set(false);
        armClosed.set(true);
    }


    private void setPower(double power){
        intakeMot.set(power);
    }
    public void debug() {
        Common.dashNum("Intake Power", intakeMot.get());
        Common.dashBool("Intake has game piece", hasGamePiece());
        Common.dashNum("Intake state", state);
        Common.dashBool("Intake has hatch", hasHatch());
        Common.dashBool("Intake has ball", hasBall());
        Common.dashNum("Ir reading", getDistance());
        Common.dashNum("Raw IR", irInput.getVoltage());
    }
}