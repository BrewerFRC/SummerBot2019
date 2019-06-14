package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.*;

public class Intake {
    private static final Spark intakeMot = new Spark(Constants.INTAKE_1);
    public static final DigitalInput intakeLimit = new DigitalInput(Constants.INTAKE_LIMIT);

    private final double  P_BALL_INTAKE = -1.0, P_HATCH_INTAKE = -1.0,
    P_BALL_SHOOT = 1.0, P_HATCH_PLACE = 1.0;
    public static final double BALL_DISTANCE = 5;
    private Solenoid armClosed;
    private Solenoid armOpen;

    private AnalogInput irInput;
    public enum intakeStates {
        IDLE,
        HATCH_INTAKE, //intake wheels until limit switch triggered pnu closed
        BALL_INTAKE, //intake wheels until ir sensor triggered pnu open
        HAS_HATCH,
        HAS_BALL,
        HATCH_PLACE, //wheels out till time or limit switch pnu closed
        BALL_PLACE; //spin wheels out till time or ir sensor pnu open
    }
    public intakeStates state = intakeStates.IDLE;

    
    private double previousReading = 0;

    public Intake() {
        irInput = new AnalogInput(Constants.IR_PORT);
        
        intakeMot.setInverted(true);

        armOpen = new Solenoid(Constants.ARM_PNU_OPEN);
        armClosed = new Solenoid(Constants.ARM_PNU_CLOSE);
    }
    public double toDist(double input){
        double out = 23.1186 * Math.pow(0.999014, input);

        return out;
    }
    public boolean hasBall() {
        if(toDist(irInput.getVoltage()) <= BALL_DISTANCE) {
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
            pnuClose();
            setPower(this.P_HATCH_INTAKE);//Should pull in
            state = intakeStates.HATCH_INTAKE;
        }
    }

    public void intakeBall() {
        if (!hasGamePiece()) {
            pnuOpen();
            setPower(this.P_BALL_INTAKE);//Should pull in
            state = intakeStates.BALL_INTAKE;
        }
    }

    public void placeHatch() {
        if (hasHatch() && state == intakeStates.HAS_HATCH) { //Same thing
            state = intakeStates.HATCH_PLACE;
            setPower(this.P_HATCH_PLACE);
        }
    }

    public void update() {
        switch (state) {
        case IDLE:
            if (hasBall()) {
                state = intakeStates.HAS_BALL;
            } else if (hasHatch()) {
                state = intakeStates.HAS_HATCH;
            }
            break;
        case HATCH_INTAKE:
            pnuClose();
            setPower(-1.0);
            if (hasHatch()) {
                state = intakeStates.HAS_HATCH;
            }
            break;
        case BALL_INTAKE:
            pnuOpen();
            setPower(-1.0);
            if (hasBall()) {
                state = intakeStates.HAS_BALL;
            }
            break;
        case HAS_BALL:
            //Not sure about this
            if (!hasBall()) {
                state = intakeStates.IDLE;
            }
            break;
        case HAS_HATCH:
            if (!hasHatch()) {
                state =  intakeStates.IDLE;
            }
            break;
        case HATCH_PLACE:
            setPower(this.P_HATCH_PLACE);
            if (!hasHatch()) {
                state =  intakeStates.IDLE;
                setPower(0);
            }
            break;
        case BALL_PLACE:
            setPower(this.P_BALL_SHOOT);
            if (!hasBall()) {
                state =  intakeStates.IDLE;
                setPower(0);
            }
            break;
        }
    }

    public boolean hasGamePiece() {
        return hasBall() || hasHatch();
    }

    private void pnuOpen() {
        armOpen.set(true);
        armClosed.set(false);
    }


    private void pnuClose() {
        armOpen.set(false);
        armClosed.set(true);
    }


    private void setPower(double power){
        intakeMot.set(power);
        Common.dashNum("intake power", power);
    }
}