package frc.robot;

import edu.wpi.first.wpilibj.Spark;

/**
 * Class to control a climber 
 * 
 * @author Brewer FRC Orange Chaos
 * @author Brent Roberts
 */
public class Climber {
    private static final Spark climberMotor = new Spark(Constants.CLIMBER);
    private final double EXTEND_POWER = 0.2, RETRACT_POWER = -0.2;

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

    private void setPower(double power) {
        Common.dashNum("Climber Power", power);
        climberMotor.set(power);
    }

    public void startExtend() {
        Common.debug("Climber starting to extend");
        state = ClimberStates.EXTEND;
    }

    public void startRetract() {
        Common.debug("Climber starting to retract");
        state = ClimberStates.RETRACT;
    }

    public void toIdle() {
        Common.debug("Climber moving to IDLE");
        state = ClimberStates.IDLE;
    }


    public void update() {
        Common.dashStr("Climber State", state.toString());
        switch(state) {
            case IDLE:
                setPower(0.0);
            break;
            case EXTEND:
                setPower(this.EXTEND_POWER);
            break;
            case RETRACT:
                setPower(this.RETRACT_POWER);
            break;

        }
    }

}