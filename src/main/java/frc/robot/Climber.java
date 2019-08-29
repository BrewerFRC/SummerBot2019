package frc.robot;

import java.sql.Time;

import edu.wpi.first.wpilibj.Spark;

/**
 * Class to control a climber 
 * 
 * @author Brewer FRC Orange Chaos
 * @author Brent Roberts
 */
public class Climber {
    private static final Spark climberMotor = new Spark(Constants.CLIMBER);
    private static final Spark climberMotor2 = new Spark(Constants.CLIMBER2);
    private final double EXTEND_POWER = -0.9, RETRACT_POWER = 0.18;
    private long climbTime;

    public enum ClimberStates {
        IDLE,
        EXTEND,
        RETRACT
    }
    private ClimberStates state = ClimberStates.IDLE;

    /**
     * Instantaties climber
     */
    public Climber() {

    }

    public void init() {
        startRetract();
    }

    private void setPower(double power) {
        Common.dashNum("Climber Power", power);
        climberMotor.set(power);
        climberMotor2.set(power);
    }

    public void startExtend() {
        if (state != ClimberStates.EXTEND) {
            Common.debug("Climber starting to extend");
            climbTime = Common.time()+1250;
            state = ClimberStates.EXTEND;
        } else {
            //Common.debug("Climber tried to extend while extending.");
        }
    }

    public void startRetract() {
        Common.debug("Climber starting to retract");
        state = ClimberStates.RETRACT;
    }

    public void toIdle() {
        if (state != ClimberStates.EXTEND){
            state = ClimberStates.IDLE;
            Common.debug("Climber moving to IDLE");
        }
    }


    public void update() {
        Common.dashStr("Climber State", state.toString());
        switch(state) {
        case EXTEND:
            setPower(EXTEND_POWER);
            if (Common.time() >= climbTime) {
                state = ClimberStates.IDLE;
            }
            break;
        case RETRACT:
            setPower(RETRACT_POWER);
            break;
        case IDLE:
            setPower(0.0);
            break;

        }
    }

}