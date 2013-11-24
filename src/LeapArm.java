import lejos.nxt.Motor;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;


public class LeapArm {
	
	/*
	 * Constants
	 */
	final static int CLAW_MAX_SPEED = 200;
	final static int CLAW_MIN_SPEED = 0;
	final static int CLAW_MAX_ANGLE = 150;
	final static int CLAW_MIN_ANGLE = 0;
	
	final static float Z_MAX = 300;
	final static float Z_MIN = 100;
	
	final static int ARM_MAX_SPEED = 100;
	final static int ARM_MIN_SPEED = 0;
	final static int ARM_MAX_ANGLE = 85;
	final static int ARM_MIN_ANGLE = 0;
	
	final static float Y_MAX = 300;
	final static float Y_MIN = 125;
	
	final static int ROTATE_MAX_SPEED = 100;
	final static int ROTATE_MIN_SPEED = 0;
	final static int ROTATE_MAX_ANGLE = 100;
	final static int ROTATE_MIN_ANGLE = 0;
	
	final static float X_MAX = 100;
	final static float X_MIN = -100;
	
	/*
	 * Leap motion things
	 */
	Hand hand;
	Controller controller;
	
	public static void main(String[] args) {
		new LeapArm();
	}
	public LeapArm() {
		// Attach a listener to the leap controller
		FrameListener listener = new FrameListener();
		controller = new Controller();
		controller.addListener(listener);
		// reset motor encoder
		Motor.A.resetTachoCount();
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		
		
		// make them stop
		Motor.A.stop();
		Motor.B.stop();
		Motor.C.stop();
		
		// set the default speeds
		Motor.A.setSpeed(250);
		Motor.B.setSpeed(50);
		Motor.C.setSpeed(50);
		
		// begin loop
		/*
		 * Quick note here, the Lego NXT is a sequentially programmed thing. So you give it a command and it waits until it is done.
		 * Then you can give it another command. The leap is the opposite in that it uses listeners and callbacks to handle input. So the leap
		 * needs a loop and the NXT needs a sequential set of instructions. Thankfully we have the yield() command. This relieves the system to 
		 * the NXT so that it can do its thing and then returns to hande input from the leap. This isnt perfect though because of a weakness in
		 * the NXT which causes it to crash sometimes. 
		 */
		boolean running = true;
		while(running) {
			// we have a hand to read
			if(hand!=null && hand.palmPosition()!=null) {
				// determine where we should move the arm to
				clawTarget = (int) GetAngleFromZ((int)hand.palmPosition().getZ()+125);
				armTarget = (int) GetAngleFromY((int)hand.palmPosition().getY());
				rotateTarget = (int) GetAngleFromX((int)hand.palmPosition().getX());
				System.out.println(Math.abs(clawTarget-clawAngle)+", "+Math.abs(armTarget-armAngle)+", "+Math.abs(rotateTarget-rotateAngle));
				
				// we need to only move one motor at a time. This is another weakness in the NXT. I think it has something to do with the tachometers
				// getting confused with each other.
				if(Math.abs(clawTarget-clawAngle) > Math.abs(armTarget-armAngle) && Math.abs(clawTarget-clawAngle) > Math.abs(rotateTarget-rotateAngle)) {
					MoveClaw();
				} else if (Math.abs(armTarget-armAngle) > Math.abs(clawTarget-clawAngle) && Math.abs(armTarget-armAngle) > Math.abs(rotateTarget-rotateAngle)){
					MoveArm();
				} else if (Math.abs(rotateTarget-rotateAngle) > Math.abs(clawTarget-clawAngle) && Math.abs(rotateTarget-rotateAngle) > Math.abs(armTarget-armAngle)){
					//Rotate();
				} else {
					Motor.A.stop();
					Motor.B.stop();
					Motor.C.stop();
				}
				
				
			} else {
				// no hand input so freeze the motors
				Motor.A.stop();
				Motor.B.stop();
				Motor.C.stop();
			}
			// give up the loop and give control to the rest of the program
			Thread.yield();
		}
	}
	int clawTarget = 0;
	int clawAngle = 0;
	public void MoveClaw() {
		//System.out.println("A tacho count: " +Motor.A.getTachoCount() + ", " + clawTarget);
		clawAngle = Motor.A.getTachoCount();
		Motor.A.setSpeed(GetClawSpeedFromAngleDistance(Math.abs(clawTarget-clawAngle)));

		// Dont move is the distance is less than 1 or if the motor cant physically move
		if(Math.abs(clawTarget-clawAngle) < 1 || Motor.A.isStalled()) {
			Motor.A.stop();
			return;
		}
		
		if(clawTarget-clawAngle < 0) {
			Motor.A.backward();
		} else if(clawTarget-clawAngle > 0) {
			Motor.A.forward();
		} else {
			Motor.A.stop();
		}
	}
	public float GetAngleFromZ(int z) {
		if(z > Z_MAX) {
			return CLAW_MAX_ANGLE;
		}
		if(z < Z_MIN) {
			return CLAW_MIN_ANGLE;
		}
		return (z-Z_MIN)*(CLAW_MAX_ANGLE/(Z_MAX-Z_MIN));
	}
	public int GetClawSpeedFromAngleDistance(int distance) {
		int speed = (int) (-0.011*(distance*distance)+2.9571*distance+0);
		if(speed < CLAW_MIN_SPEED) {
			speed = CLAW_MIN_SPEED;
		}
		
		return speed;
	}
	int armTarget = 0;
	int armAngle = 0;
	public void MoveArm() {
		//System.out.println("B tacho count: " +Motor.B.getTachoCount() + ", " + armTarget);
		armAngle = Motor.B.getTachoCount();
		Motor.B.setSpeed(GetArmSpeedFromAngleDistance(Math.abs(armTarget-armAngle)));

		// Dont move is the distance is less than 1 or if the motor cant physically move
		if(Math.abs(armTarget-armAngle) < 1 || Motor.B.isStalled()) {
			Motor.B.stop();
			return;
		}
		
		if(armTarget-armAngle < 0) {
			Motor.B.backward();
		} else if(armTarget-armAngle > 0) {
			Motor.B.forward();
		} else {
			Motor.B.stop();
		}
	}
	public float GetAngleFromY(int y) {
		if(y > Y_MAX) {
			return ARM_MIN_ANGLE;
		}
		if(y < Y_MIN) {
			return ARM_MAX_ANGLE;
		}
		return ((y-Y_MAX)*((ARM_MAX_ANGLE)/(Y_MIN-Y_MAX)));
	}
	public int GetArmSpeedFromAngleDistance(int distance) {
		int speed = (int) (-0.011*(distance*distance)+2.9571*distance+0);
		if(speed < ARM_MIN_SPEED) {
			speed = ARM_MIN_SPEED;
		}
		
		return speed;
	}
	int rotateTarget = 0;
	int rotateAngle = 0;
	public void Rotate() {
		//System.out.println("C tacho count: " +Motor.C.getTachoCount() + ", " + Math.abs(rotateTarget-rotateAngle));
		rotateAngle = Motor.C.getTachoCount();
		Motor.C.setSpeed(GetRotateSpeedFromAngleDistance(Math.abs(Math.abs(rotateTarget-rotateAngle)-rotateAngle)));

		// Dont move is the distance is less than 1 or if the motor cant physically move
		if(Math.abs(Math.abs(rotateTarget-rotateAngle)-rotateAngle) < 1 || Motor.C.isStalled()) {
			Motor.C.stop();
			return;
		}
		
		if(Math.abs(rotateTarget-rotateAngle)-rotateAngle < 0) {
			Motor.C.backward();
		} else if(rotateTarget-rotateAngle > 0) {
			Motor.C.forward();
		} else {
			Motor.C.stop();
		}
	}
	public float GetAngleFromX(int x) {
		System.out.println("x = "+x);
		if(x > X_MAX) {
			return ROTATE_MAX_ANGLE;
		}
		if(x < X_MAX) {
			return ROTATE_MIN_ANGLE;
		}
		return ((x-X_MAX)*((ROTATE_MAX_ANGLE)/(X_MIN-X_MAX)));
	}
	public int GetRotateSpeedFromAngleDistance(int distance) {
		int speed = (int) (-0.011*(distance*distance)+2.9571*distance+0);
		if(speed < ROTATE_MIN_SPEED) {
			speed = ROTATE_MIN_SPEED;
		}
		
		return speed;
	}
	
	class FrameListener extends Listener
	{
	    public void onFrame(Controller controller)
	    {
	    	 Frame frame = controller.frame(); //The latest frame
	    	 if (!frame.hands().isEmpty()) {
	    		 // just use the first hand we find
		     	hand = frame.hands().get(0);
		     } else {
		        hand = null;
		     }
	    }
	};
	
}
