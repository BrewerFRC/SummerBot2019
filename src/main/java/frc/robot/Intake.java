package frc.robot;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.*;

public class Intake {
    public static final Spark INTAKEMOT = new Spark(Constants.INTAKE_1);

    public static double RECIEVE_SPEED = 0.65f;
    public static double HOLD_SPEED = 0.25f;
    public static double SOFT_THROW_SPEED = -0.6f;
    public static final double FULL_SEND_SPEED = -1f;
    private Solenoid armClosed;
    private Solenoid armOpen;

    private AnalogInput irInput; 

    
    private double previousReading = 0;

    public Intake() {
        irInput = new AnalogInput(Constants.IR_PORT);

        armOpen = new Solenoid(Constants.ARM_PNU_OPEN);
        armClosed = new Solenoid(Constants.ARM_PNU_CLOSE);
    }
}