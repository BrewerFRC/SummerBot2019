package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Xbox.buttons;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.cscore.UsbCamera;

public class Robot extends TimedRobot {
  //public static final Spark INTAKEMOT = new Spark(Constants.INTAKE);
  private Arm arm = new Arm();
  private Intake intake = new Intake();
  private Xbox front = new Xbox(0);
  private Xbox back = new Xbox(1);
  private DriveTrain dt = new DriveTrain();
  private Climber climber = new Climber();
  private Compressor compressor;
  //private Solenoid in;
  //private Solenoid out;
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
    CameraServer.getInstance().startAutomaticCapture(0);
    //in = new Solenoid(1); 
    //out = new Solenoid(0);
  }

  @Override
  public void disabledPeriodic() {
    arm.debug();
    intake.debug();
    if (front.getPressed(Xbox.buttons.start)) {
      Common.debug("start button pressed");
    }
  }

 
  @Override
  public void robotPeriodic() {
  }


  @Override
  public void autonomousInit() {
   arm.init();
  }

  @Override
  public void autonomousPeriodic() {
    teleOpCode();
  }

  @Override
  public void teleopInit() {
    arm.init();
    climber.startRetract();
  }

  public void teleOpCode() {
    //arm.camServ.setAngle((front.getX(GenericHID.Hand.kRight)+1) * 180);//VERY BAD NO DO
    Common.dashNum("Servo angle", arm.camServ.getAngle());

    compressor.setClosedLoopControl(true);

    double forward = 0;
		double turn = 0;
		
    
    if (Math.abs(front.getY(GenericHID.Hand.kLeft)) > 0.15 || Math.abs(front.getX(GenericHID.Hand.kLeft)) > 0.15) {
      forward = front.deadzone(front.getY(GenericHID.Hand.kLeft));
      turn = front.deadzone(front.getX(GenericHID.Hand.kLeft));
    } else {
      forward = back.deadzone(back.getY(GenericHID.Hand.kLeft));
      turn = back.deadzone(back.getX(GenericHID.Hand.kLeft));
    }
    
    dt.accelDrive(-forward *0.75, -turn *0.75);
    
    /*
    if (driver.getPressed(Xbox.buttons.x)) {
      Common.dashNum("intake power", 1);
      INTAKEMOT.set(1); 
    }
    else if (driver.getPressed(Xbox.buttons.y)) {
      INTAKEMOT.set(-1);
      Common.dashNum("intake power", -1);
    }
    else{
      INTAKEMOT.set(0);
    }
    */
    /*
    if (driver.getPressed(Xbox.buttons.a)) {
      //Common.debug("INTAKING HATCHES");
      intake.intakeHatch();
    }
    else if (driver.getPressed(Xbox.buttons.b)) {
      intake.stopIntake();
    } else if (driver.getPressed(Xbox.buttons.rightTrigger)) {
      intake.placeGamePiece();
    } else if (driver.getPressed(Xbox.buttons.x)) {
      intake.intakeBall();
    }
    intake.update();
    intake.debug();
    
    if (driver.getPressed(Xbox.buttons.dPadLeft)) {
      //Common.debug("x");
      arm.setTarget(-90);
    } else if (driver.getPressed(Xbox.buttons.dPadUp)) {
      //Common.debug("y");
      arm.setTarget(0);
    } else if (driver.getPressed(Xbox.buttons.dPadRight)) {
      //Common.debug("b");
      arm.setTarget(90);
    } else if (driver.getPressed(Xbox.buttons.dPadDown)) {
      Common.debug("Down");
      arm.setTarget(120);
    }
    */
    
    if (front.getPressed(Xbox.buttons.y)) {
        arm.camTargetFront();
        arm.setTarget(0);
    }
    if (front.getPressed(Xbox.buttons.b)) {
        if (intake.hasBall()) {
          arm.camTargetFront();
          arm.setTarget(30);
        } else { //assume we have a hatch
          arm.camTargetFront();
          arm.setTarget(90);
        }
    }
    if (front.getPressed(Xbox.buttons.a)) {
        if (intake.hasBall()) {
          arm.camTargetFront();
          arm.setTarget(70);
        } else { //assume we have hatch
          arm.camTargetFront();
          arm.setTarget(90);
        }
    }
    if (front.getPressed(Xbox.buttons.rightBumper)) {
        arm.camTargetFront();
        arm.setTarget(115); //was 120
        intake.intakeBall();
    }
    if (front.getPressed(Xbox.buttons.leftBumper)) {
        arm.camTargetFront();
        arm.setTarget(90);
        intake.intakeHatch();
    }
    if (front.getPressed(Xbox.buttons.rightTrigger)) {
        arm.camTargetFront();
        intake.placeGamePiece();
    }
    if (front.getPressed(Xbox.buttons.x)) {
        arm.camTargetFront();
        intake.stopIntake();
    }
    /*if (front.getPressed(Xbox.buttons.dPadUp)) {
        arm.camTargetFront();
    }*/
    if (front.when(Xbox.buttons.leftThumb)) {
      dt.toggleShift();
    }

    // CLIMBER
    
    if (front.getPressed(Xbox.buttons.start) && front.getPressed(Xbox.buttons.back)) {
      Common.debug("start button appears to be pressed");
      climber.startExtend();
    }
    climber.update();


    //BACK STUFF

    if (back.getPressed(Xbox.buttons.y)) {
      arm.camTargetFront();
      arm.setTarget(0);
  }
  if (back.getPressed(Xbox.buttons.b)) {
      if (intake.hasBall()) {
        arm.camTargetFront();
        arm.setTarget(30);
      } else { //assume we have a hatch
        arm.camTargetFront();
        arm.setTarget(90);
      }
  }
  if (back.getPressed(Xbox.buttons.a)) {
      if (intake.hasBall()) {
        arm.camTargetFront();
        arm.setTarget(70);
      } else { //assume we have hatch
        arm.camTargetFront();
        arm.setTarget(90);
      }
  }
  if (back.getPressed(Xbox.buttons.rightBumper)) {
      arm.camTargetFront();
      arm.setTarget(115);
      intake.intakeBall();
  }
  if (back.getPressed(Xbox.buttons.leftBumper)) {
      arm.camTargetFront();
      arm.setTarget(90);
      intake.intakeHatch();
  }
  if (back.getPressed(Xbox.buttons.rightTrigger) || front.getPressed(Xbox.buttons.leftTrigger)) {
    arm.camTargetFront();
    intake.placeGamePiece();
  }
  if (back.getPressed(Xbox.buttons.x)) {
    arm.camTargetFront();
    intake.stopIntake();
  }
  /*if (back.getPressed(Xbox.buttons.dPadUp)) {
    arm.camTargetFront();
  }*/
  if (back.when(Xbox.buttons.leftThumb)) {
    dt.toggleShift();
  }

  


    Common.dashNum("Avg DT counts.", dt.getAverageDist() );
    intake.update();
    intake.debug();
    climber.update();
    arm.update();
    arm.debug(); 
    /*
    if (driver.when(Xbox.buttons.y)) {
      //open
      [\]
      [\]out.set(true);
      in.set(false);
    } else if (driver.when(Xbox.buttons.x)) {
      //close
      out.set(false);
      in.set(true);
    }
      */
  }

  @Override
  public void teleopPeriodic() {
    teleOpCode();
  }
  
  @Override
  public void testPeriodic() {
  }
}
