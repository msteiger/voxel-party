package nexus.model.structs;

/**
 * Container for an OpenGL RGBA Color
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 *
 */

import static org.lwjgl.opengl.GL11.glColor4f;

import org.terasology.math.TeraMath;

public class Color {
	public float r;
	public float g;
	public float b;
	public float a;
	
	/**
	 * Contructs a Color with RBGA
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public Color(float r, float g, float b, float a) {
		this.r = TeraMath.clamp(r, 0f, 1f);
		this.g = TeraMath.clamp(g, 0f, 1f);
		this.b = TeraMath.clamp(b, 0f, 1f);
		this.a = TeraMath.clamp(a, 0f, 1f);
	}
	
	/**
	 * Constructs a Color with RGB and infers 1.0 for A
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public Color(float r, float g, float b) {
		this(r, g, b, 1.0f);
	}
	
	/**
	 * Sets the OpenGL Color to this Color
	 */
	public void color() {
		glColor4f(this.r, this.g, this.b, this.a);
	}
}
