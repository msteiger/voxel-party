package nexus.view.gl;

/**
 * A collection of static helper methods for drawing 2D shapes
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 * 
 */

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;
import nexus.model.structs.Color;

public class Planes {
	
	/**
	 * Draws a rectangle
	 */
	public static void drawQuad2f(float ax, float ay, float az, float bx, float by, float bz, Color color) {
		glBegin(GL_TRIANGLES);
		
		color.color();
		
		if (az == bz) {
			glVertex3f(ax, ay, az);
			glVertex3f(ax, by, az);
			glVertex3f(bx, by, az);
			
			glVertex3f(bx, by, az);
			glVertex3f(ax, ay, az);
			glVertex3f(bx, ay, az);
		} else if (ay == by) {
			glVertex3f(ax, ay, az);
			glVertex3f(ax, ay, bz);
			glVertex3f(bx, ay, bz);
			
			glVertex3f(ax, ay, az);
			glVertex3f(bx, ay, az);
			glVertex3f(bx, ay, bz);	
		} else {
			glVertex3f(ax, ay, az);
			glVertex3f(ax, by, az);
			glVertex3f(ax, by, bz);
			
			glVertex3f(ax, by, bz);	
			glVertex3f(ax, ay, az);
			glVertex3f(ax, ay, bz);
		}
		
		glEnd();
	}
}
