package nexus.model.renderable;

import nexus.model.structs.Block;
import nexus.model.structs.Color;
import nexus.model.structs.Vector3;

public class Solid extends Block {

	public Solid(Vector3 a, float dimension, Color color) {
		super(a, dimension, getColor(color));
	}

	/**
	 * @param color
	 * @return
	 */
	private static Color getColor(Color block)
	{
        float f = 0.1f;
        float r = (float) (block.r + Math.random() * f);
        float g = (float) (block.g + Math.random() * f);
        float b = (float) (block.b + Math.random() * f);
		return new Color(r, g, b);
	}
}
