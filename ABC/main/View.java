package main;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class View {
	Model model;
	int width, height;
	boolean stop = false;

	public View(Model model, int width, int height) {
		this.model = model;
		this.width = width;
		this.height = height;
	}

	public void init() {
		/* OpenGL */
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		
		glViewport(0, 0, width, height); // Reset The Current Viewport
		glMatrixMode(GL_PROJECTION); // Select The Projection Matrix
		glLoadIdentity(); // Reset The Projection Matrix
		GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
		glLoadIdentity(); // Reset The Modelview Matrix

		glShadeModel(GL_SMOOTH); // Enables Smooth Shading
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST); // Enables Depth Testing
		glDepthFunc(GL_LEQUAL); // The Type Of Depth Test To Do
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Really Nice Perspective Calculations
	
		this.run();
	}

	public void run() {
		
		while (!Display.isCloseRequested() && !this.stop) {
			Display.sync(60);
			this.render();
		}

		Display.destroy();
	}

	public void render() {
		if(!model.locked) {
			// Clear the screen and depth buffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	

//			model.player.draw3D();
			
	         glLoadIdentity();                                         //Reset The View
	         glTranslatef(-1.5f,0.0f,-8.0f);						// Move Left 1.5 Units And Into The Screen 6.0
//	         glRotatef(rtri,0.0f,1.0f,0.0f);						// Rotate The Triangle On The Y axis ( NEW )
	         glBegin(GL_TRIANGLES);								// Start Drawing A Triangle
	         glColor3f(1.0f,0.0f,0.0f);						// Red
	         glVertex3f( 0.0f, 1.0f, 0.0f);					// Top Of Triangle (Front)
	         glColor3f(0.0f,1.0f,0.0f);						// Green
	         glVertex3f(-1.0f,-1.0f, 1.0f);					// Left Of Triangle (Front)
	         glColor3f(0.0f,0.0f,1.0f);						// Blue
	         glVertex3f( 1.0f,-1.0f, 1.0f);					// Right Of Triangle (Front)
	         glColor3f(1.0f,0.0f,0.0f);						// Red
	         glVertex3f( 0.0f, 1.0f, 0.0f);					// Top Of Triangle (Right)
	         glColor3f(0.0f,0.0f,1.0f);						// Blue
	         glVertex3f( 1.0f,-1.0f, 1.0f);					// Left Of Triangle (Right)
	         glColor3f(0.0f,1.0f,0.0f);						// Green
	         glVertex3f( 1.0f,-1.0f, -1.0f);					// Right Of Triangle (Right)
	         glColor3f(1.0f,0.0f,0.0f);						// Red
	         glVertex3f( 0.0f, 1.0f, 0.0f);					// Top Of Triangle (Back)
	         glColor3f(0.0f,1.0f,0.0f);						// Green
	         glVertex3f( 1.0f,-1.0f, -1.0f);					// Left Of Triangle (Back)
	         glColor3f(0.0f,0.0f,1.0f);						// Blue
	         glVertex3f(-1.0f,-1.0f, -1.0f);					// Right Of Triangle (Back)
	         glColor3f(1.0f,0.0f,0.0f);						// Red
	         glVertex3f( 0.0f, 1.0f, 0.0f);					// Top Of Triangle (Left)
	         glColor3f(0.0f,0.0f,1.0f);						// Blue
	         glVertex3f(-1.0f,-1.0f,-1.0f);					// Left Of Triangle (Left)
	         glColor3f(0.0f,1.0f,0.0f);						// Green
	         glVertex3f(-1.0f,-1.0f, 1.0f);					// Right Of Triangle (Left)
	         glEnd();		

			Display.update();
		}
	}

	public void stop() {
		this.stop = true;
	}
}