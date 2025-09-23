package com.ibm.conn.auto.config;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class RobotTypeClass{

    private Robot robot;

    public RobotTypeClass(Robot robot){
        this.robot = robot;
    }

    public void typeMessage(String message){
        for (int i = 0; i < message.length(); i++){
            handleRepeatCharacter(message, i);
            type(message.charAt(i));
        }
    }

    private void handleRepeatCharacter(String message, int i){
        if(i == 0)
            return;
        //The robot won't type the same letter twice unless we release a key.
        if(message.charAt(i) == message.charAt(i-1)){
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }

    private void type(char character){
        handleSpecialCharacter(character);

        if (Character.isLowerCase(character)){
            typeCharacter(Character.toUpperCase(character));
        }
        if (Character.isUpperCase(character)){
            typeShiftCharacter(character);
        }
        if (Character.isDigit(character)){
            typeCharacter(character);
        }
    }

    private void handleSpecialCharacter(char character){
        if (character == ' ')
            typeCharacter(KeyEvent.VK_SPACE);
        if (character == '.')
            typeCharacter(KeyEvent.VK_PERIOD);
        if (character == '!')
            typeShiftCharacter(KeyEvent.VK_1);
        if (character == '?')
            typeShiftCharacter(KeyEvent.VK_SLASH);
        if (character == ',')
        	typeCharacter(KeyEvent.VK_COMMA);
        if (character == ':')
        	typeShiftCharacter(KeyEvent.VK_SEMICOLON);
        if (character == '\\')
            typeCharacter(KeyEvent.VK_BACK_SLASH);
//C:\SeleniumServer\TestFiles
        //More specials here as needed
    }

    private void typeCharacter(int character){
        robot.keyPress(character);
        robot.delay(100);
        robot.keyRelease(character);
        
    }

	private void typeShiftCharacter(int character){
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(character);
        robot.keyRelease(character);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.delay(100);
    }
	
	public void tabToButtonAndEnter(int j){
			
		for (int i = 0; i < j; i++){
			//robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
			robot.delay(1000);
			//robot.keyRelease(KeyEvent.VK_CONTROL);
		}
		
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.delay(1000);
	}
	
	public void CancelOSDialog(){
		robot.delay(1500);
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);
		robot.delay(3000);
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_C);
		robot.keyRelease(KeyEvent.VK_ALT);
		robot.keyRelease(KeyEvent.VK_C);
		robot.delay(2000);
	}

}
