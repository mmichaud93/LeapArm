import java.io.IOException;

import lejos.nxt.Motor;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;


public class LeapArm {
	Hand hand;
	Controller controller;
	float startZ = 0;
	public static void main(String[] args) {
		new LeapArm();
	}
	public LeapArm() {
		System.out.println("Start");
		//FrameListener listener = new FrameListener();
		controller = new Controller();
		//controller.addListener(listener);
		Motor.A.resetTachoCount();
		Motor.B.resetTachoCount();
		Motor.C.resetTachoCount();
		
		Motor.A.flt();
		Motor.B.flt();
		
		Motor.A.setSpeed(78);
		Motor.B.setSpeed(35);
		Motor.C.setSpeed(110);
		
		OpenClaw();
		LowerArm();
		CloseClaw();
		RaiseArm();
		RotateLeft();
		//LowerOpenLeft();
		//RaiseCloseRight();
		LowerArm();
		OpenClaw();
		RaiseArm();
		CloseClaw();
		
		RotateRight();
	}
	public void UseHand() {
		if(hand!=null) {
			System.out.println(hand.palmPosition());
			Vector h = hand.stabilizedPalmPosition();
			
			if (h.getY() < 140 || h.getY() > 180) { //if this is true, we can move [attempt to define a stationary bounding box]
				if(h.getY() < 160) {
					// hand down
					Motor.B.rotateTo(80);
				} else if(h.getY() > 160) {
					//  hand up
					Motor.B.rotateTo(0);
				} 
			}
			if(h.getZ() < -30 || h.getZ() > 30) {
				if(h.getZ() < 0) {
					// hand forward
					Motor.A.rotateTo(0);
				} else if(h.getZ() > 0) {
					// hand backward
					Motor.A.rotateTo(80);
				}
			}
			if(h.getX() < -10 || h.getX() > 10) {
				if(h.getX() < 0) {
					// hand left
				} else if(h.getX() > 0) {
					// hand right
				}
			}
		} else {
			if(Motor.A.isMoving())
				Motor.A.stop();
			if(Motor.B.isMoving())
				Motor.B.stop();
		}
		
	}
	
	public void LowerOpenLeft() {
		boolean lowerDone = false;
		boolean openDone = false;
		boolean leftDone = false;
		while(!lowerDone || !openDone || !leftDone) {
			System.out.println("A = "+Motor.A.getTachoCount());
			if(!openDone) {
				if(Motor.A.getTachoCount()-MotorAStartAngle < 78 && !Motor.A.isStalled()) {
					if(!Motor.A.isMoving()) {
						Motor.A.forward();
					}
				} else {
					Motor.A.stop();
					openDone = true;
				}
			}
			System.out.println("B = "+Motor.B.getTachoCount());
			if(!lowerDone) {
				if(Motor.B.getTachoCount()-MotorBStartAngle < 35 && !Motor.B.isStalled()) {
					if(!Motor.B.isMoving()) {
						Motor.B.forward();
					}
				} else {
					Motor.B.stop();
					lowerDone = true;
				}
			}
			System.out.println(Motor.C.getTachoCount());
			if(!leftDone) {
				if(Motor.C.getTachoCount()-MotorAStartAngle < 120 && !Motor.C.isStalled()) {
					if(!Motor.C.isMoving()) {
						Motor.C.forward();
					}
				} else {
					Motor.C.stop();
					leftDone = true;
				}
			}
		}
	}
	public void RaiseCloseRight() {
		boolean raiseDone = false;
		boolean closeDone = false;
		boolean rightDone = false;
		while(!raiseDone || !closeDone || !rightDone) {
			System.out.println("A = "+Motor.A.getTachoCount());
			if(!closeDone) {
				if(Motor.A.getTachoCount()-MotorAStartAngle > 2 && !Motor.A.isStalled()) {
					if(!Motor.A.isMoving()) {
						Motor.A.backward();
					}
				} else {
					Motor.A.stop();
					closeDone = true;
				}
			}
			System.out.println("B = "+Motor.B.getTachoCount());
			if(!raiseDone) {
				if(Motor.B.getTachoCount()-MotorBStartAngle > 2 && !Motor.B.isStalled()) {
					if(!Motor.B.isMoving()) {
						Motor.B.backward();
					}
				} else {
					Motor.B.stop();
					raiseDone = true;
				}
			}
			System.out.println(Motor.C.getTachoCount());
			if(!rightDone) {
				if(Motor.C.getTachoCount()-MotorAStartAngle > 4 && !Motor.C.isStalled()) {
					if(!Motor.C.isMoving()) {
						Motor.C.backward();
					}
				} else {
					Motor.C.stop();
					rightDone = true;
				}
			}
		}
	}
	int MotorAStartAngle = 0;
	int MotorBStartAngle = 0;
	int MotorCStartAngle = 0;
	public void RotateRight() {
		System.out.println(Motor.C.getTachoCount());
		while(Motor.C.getTachoCount()-MotorAStartAngle > 0 && !Motor.C.isStalled()) {
			if(!Motor.C.isMoving()) {
				Motor.C.backward();
			}
		}
		Motor.C.stop();
	}
	public void RotateLeft() {
		System.out.println(Motor.C.getTachoCount());
		while(Motor.C.getTachoCount()-MotorAStartAngle < 120 && !Motor.C.isStalled()) {
			if(!Motor.C.isMoving()) {
				Motor.C.forward();
			}
		}
		Motor.C.stop();
	}
	public void OpenClaw() {
		System.out.println(Motor.A.getTachoCount());
		while(Motor.A.getTachoCount()-MotorAStartAngle < 78 && !Motor.A.isStalled()) {
			if(!Motor.A.isMoving()) {
				Motor.A.forward();
			}
		}
		Motor.A.stop();
	}
	public void CloseClaw() {
		System.out.println(Motor.A.getTachoCount());
		while(Motor.A.getTachoCount()-MotorAStartAngle > 2 && !Motor.A.isStalled()) {
			if(!Motor.A.isMoving()) {
				Motor.A.backward();
			}
		}
		Motor.A.stop();
	}
	public void LowerArm() {
		System.out.println(Motor.B.getTachoCount());
		while(Motor.B.getTachoCount()-MotorBStartAngle < 35 && !Motor.B.isStalled()) {
			if(!Motor.B.isMoving()) {
				Motor.B.forward();
			}
		}
		Motor.B.stop();
	}
	public void RaiseArm() {
		System.out.println("B = "+Motor.B.getTachoCount());
		while(Motor.B.getTachoCount()-MotorBStartAngle > 2 && !Motor.B.isStalled()) {
			if(!Motor.B.isMoving()) {
				Motor.B.backward();
			}
		}
		Motor.B.stop();
	}
	public void GetHand() {
		Frame frame = controller.frame(); //The latest frame
        if (!frame.hands().isEmpty()) {
            hand = frame.hands().get(0);
        } else {
        	hand = null;
        }
	}
	class FrameListener extends Listener
	{
	    public void onFrame(Controller controller)
	    {
	        
	    }
	};
	
}
