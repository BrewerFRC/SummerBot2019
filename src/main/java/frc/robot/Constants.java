package frc.robot;

public class Constants {
	public static final int

	REFRESH_RATE = 5,

			// PWM
			DRIVE_FL = 2,
			DRIVE_FR = 1,
			DRIVE_BL = 4,
			DRIVE_BR = 3,
			//ARM_M = , 

			INTAKE_1 = 5,
			// DIO
			//DRIVE_ENCODER_LA = , DRIVE_ENCODER_RA = , DRIVE_ENCODER_LB = , DRIVE_ENCODER_RB = ,

			// Analog Input
			//IR_PORT = , ARM_POT = ,

			//ARM_MIN_DEG = , ARM_MAX_DEG = ,

			// Solenoid
			GEARBOX_PNU = 2, ARM_PNU_CLOSE = 0, ARM_PNU_OPEN = 1; // open / close may not be in the correct order

	;
	public static final double

	MAX_ACCEL = 0.5,

			MIN_ARM_MAGNITUDE = 0.45, ARMP = 1, ARMI = 0, ARMD = 0,

			ARMMAXACCEL = 0.7f, MAX_ARM_VELOCITY = 5, MIN_ARM_VELOCITY = 40, MIN_ARM_ACC = 0.45,

			HEADINGP = DriveTrain.TURNMAX / 10, HEADINGI = 0, HEADINGD = 0, ARM_POT_SCALAR = 0.01889

	;
}
