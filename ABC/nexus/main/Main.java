package nexus.main;

/** 
 * Launcher for Nexus
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 * 
 */

import nexus.model.structs.Camera;
import nexus.model.structs.Vector3;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Main {
	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	public static final String WINDOW_TITLE = "Nexus";
	public static final int INIT_RENDER_DISTANCE = 6;
	public static final Vector3 INIT_CAMERA_POSITION = new Vector3(0.0f, 32.0f, 0.0f);
	public static final float INIT_SENSITIVITY = 0.3f;
	public static final boolean CAMERA_INVERT = true;
	
    public static void main(String[] argv) {
        try {
        	// initialize lwjgl
        	Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
        	Display.setTitle(WINDOW_TITLE);
        	Display.create();
        	Keyboard.create();
        	Mouse.create();
	    
        	// start the nexus.mvc framework
        	Model world = new Model(new Camera(INIT_CAMERA_POSITION, 0.0f, 0.0f, INIT_SENSITIVITY, CAMERA_INVERT));
            View view = new View(world, INIT_RENDER_DISTANCE);
            @SuppressWarnings("unused")
            Controller controller = new Controller(world);
            
            view.init();
        	
        } catch (LWJGLException e) {
        	e.printStackTrace();
        	System.exit(0);
        }
    }
}
