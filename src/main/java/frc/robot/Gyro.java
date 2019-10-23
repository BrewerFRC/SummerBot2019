package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

public class Gyro {
    
    public final double TARGET_ANGLE = 20;

    private ADXRS450_Gyro gyro;

    public Gyro() {
        gyro = new ADXRS450_Gyro();
    }    
        
    public void reset() {
        gyro.reset();
    }
       
    public void calibrate() {
        gyro.calibrate();
    }
    
    public double getAngle() {
        return gyro.getAngle();
    }

    public long getHeading() {
        long heading = Math.round(getAngle()) * 100 % 36000 / 100;
        if (heading < 0) {
            heading += 360;
        }
        return heading;
    }

    
    
    public void debug() {
       Common.dashNum("Gyro angle", getAngle());
       Common.dashNum("Gyro Heading", getHeading());
    }
    public void turn() {
        if (TARGET_ANGLE > getAngle());        
    }
    public void setTurn() {
        double changeLeft = ( -360 + (getAngle() - getHeading()) % 360);
        double changeRight = ( 360- (getHeading() - getAngle()) % 360);
    } 
    


}

 