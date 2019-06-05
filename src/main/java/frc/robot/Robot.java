package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {
  public static final Spark INTAKEMOT = new Spark(Constants.INTAKE_1);
  private Arm arm = new Arm();
  private Xbox driver = new Xbox(0);
  private DriveTrain dt = new DriveTrain();
  private Compressor compressor;
  private Solenoid in;
  private Solenoid out;
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

 
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    compressor = new Compressor();
    in = new Solenoid(1);
    out = new Solenoid(0);
  }

 
  @Override
  public void robotPeriodic() {
  }


  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  @Override
  public void teleopPeriodic() {

    compressor.setClosedLoopControl(true);

    double forward = 0;
		double turn = 0;
		forward = driver.getY(GenericHID.Hand.kLeft);
    turn = driver.getX(GenericHID.Hand.kLeft);
    
    dt.accelDrive(-forward, -turn);

    if (driver.getPressed(Xbox.buttons.a)) {
      Common.dashNum("intake power", 1);
      INTAKEMOT.set(1); 
    }
    else if (driver.getPressed(Xbox.buttons.b)) {
      INTAKEMOT.set(-1);
      Common.dashNum("intake power", -1);
    }
    else{
      INTAKEMOT.set(0);
    }

    Common.dashNum( "Arm Pot", arm.rawPot());
    Common.dashNum( "Measured Pot", arm.GetDegree());
    if (driver.when(Xbox.buttons.y)) {
      //open
      out.set(true);
      in.set(false);
    } else if (driver.when(Xbox.buttons.x)) {
      //close
      out.set(false);
      in.set(true);
    }
      
  }

  @Override
  public void testPeriodic() {
  }
}
