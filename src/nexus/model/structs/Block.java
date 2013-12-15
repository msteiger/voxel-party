package nexus.model.structs;

import nexus.model.renderable.BlockMask;
import nexus.view.gl.VertexContainer;

/**
 * A generic rectangular prism
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 *
 */

public class Block {
	public Vector3 a;
	public Vector3 b;
	public Color color;
	public BlockMask mask;
	
	/**
	 * Creates a Block
	 * 
	 * @param position near bottom right corner of the block
	 * @param width width on the x axis
	 * @param height height on the y axis
	 * @param depth depth on the z axis
	 */
	public Block(Vector3 a, float dimension, Color color) {
		this.a = a;
		this.b = new Vector3(a.x + dimension, a.y + dimension, a.z + dimension);
		this.color = color;
		this.mask = new BlockMask(this);
	}
	
	public void draw() {
		if (visible()) {
			mask.draw();	
		}
	}
	
	public boolean visible() {
		return true;
	}
	
}
