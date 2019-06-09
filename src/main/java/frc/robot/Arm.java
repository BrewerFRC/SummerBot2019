package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;

public class Arm {
    
    private AnalogInput pot;
    
    public Arm() {
        pot = new AnalogInput(Constants.ARM_POT);
    }

    public double rawPot() {
        return pot.getVoltage();
    }

    public double GetDegree() {
        return 90 - (rawPot() - 0.75) / Constants.ARM_POT_SCALAR;
    }
    public void motor(double speed) {
        speed = -speed;
        speed = Math.max(speed, -0.55);
        speed = Math.min(speed, 0.55);
        arm.setSpeed(speed);
        SmartDashboard.putNumber("safety speed", speed);
    }

}