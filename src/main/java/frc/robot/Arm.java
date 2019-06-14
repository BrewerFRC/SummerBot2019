package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

public class Arm {
    public static final double

	MAX_ACCEL = 0.5,

			MIN_ARM_MAGNITUDE = 0.45, ARMP = 1, ARMI = 0, ARMD = 0,

            ARMMAXACCEL = 0.7f, MAX_ARM_VELOCITY = 5, MIN_ARM_VELOCITY = 40, MIN_ARM_ACC = 0.45,
            
            FRONT_MAX = 4.543 , BACK_MAX = 0.0085, CENTER = 2.208, FRONT_PARALLEL = 3.918, BACK_PARALLEL = .605,
            FRONT_SAFE = 4.35, BACK_SAFE = 0.065,

            TOP_SPEED = 0.8,
            /*
            volts per degree is equal to (front paralel - back paralel) / 180
            */
            VOLTS_PER_DEGREE = (FRONT_PARALLEL - BACK_PARALLEL) / 180,

			HEADINGP = DriveTrain.TURNMAX / 10, HEADINGI = 0, HEADINGD = 0, ARM_POT_SCALAR = 0.01889

            
            
            
	;
    public static final Spark arm = new Spark(Constants.ARM_M);
    
    private AnalogInput pot;

    

    public Arm() {
        pot = new AnalogInput(Constants.ARM_POT);
        //arm.setInverted(true);
    }

    public double rawPot() {
        return pot.getVoltage();
    }

    public double getDegree() {
        return -90 + (rawPot() - BACK_PARALLEL) / VOLTS_PER_DEGREE;
    }

    public double degreesToVolts(double degrees) {
        return VOLTS_PER_DEGREE * (degrees + 90) + BACK_PARALLEL;

    }
 
    // Power arm with safety limits based on potentiometer reading and TOP_SPEED
    public void setMotor(double speed) {
        if (speed > 0) {
            if (rawPot()>FRONT_SAFE) {
                speed = 0;
            }
        } else {
            if (rawPot() < BACK_SAFE) {
                speed = 0;
            }
        }
        speed = Math.max(speed, -TOP_SPEED);
        speed = Math.min(speed, TOP_SPEED);
        arm.setSpeed(-speed);
        SmartDashboard.putNumber("arm speed", speed);
    }   

    public void debug() {
        Common.dashNum("Pot Degrees", getDegree());
        Common.dashNum("Raw Pot", rawPot());
    }
}