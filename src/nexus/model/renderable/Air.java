package nexus.model.renderable;

import nexus.model.structs.Block;
import nexus.model.structs.Vector3;

public class Air extends Block {

	public static final Block INSTANCE = new Air(new Vector3(0, 0, 0), 0);

	Air(Vector3 a, float dimension) {
		super(a, dimension, null);
	}
	
	@Override
	public boolean visible() {
		return false;
	}
}
