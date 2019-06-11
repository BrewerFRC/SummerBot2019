package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Spark;

public class Arm {
    public static final Spark arm = new Spark(Constants.ARM_M);
    
    private AnalogInput pot;
    
    public Arm() {
        pot = new AnalogInput(Constants.ARM_POT);
    }

    public void setPower(double power) {
        arm.set(power);
    }

    public double rawPot() {
        return pot.getVoltage();
    }

    public double GetDegree() {
        return 90 - (rawPot() - 0.75) / Constants.ARM_POT_SCALAR;
    }
    

}