package nexus.model.structs;

import nexus.model.generators.Perlin;
import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

/**
 * 16x16 containers for Terrain
 * 
 * @author Lane Aasen <laneaasen@gmail.com>
 *
 */

public class Chunk {
	// this value should not need to be changed
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;
	public static final int BIG_NUMBER = (int) Math.pow(2, 18);

	int x, z;
	Vector3 dilation;
	public Block[][][] blocks;
	boolean mask = false;
	ChunkContainer parent;

	/**
	 * Creates a new Chunk
	 * 
	 * @param x x position of the Chunk in its container
	 * @param z z position of the Chunk in its container
	 * @param dilation (x, y, z) dilation for terrain generatrion
	 * @param parent the Chunk's parent ChunkContainer
	 */
	public Chunk(int x, int z, Vector3 dilation, ChunkContainer parent) {
		this.x = x;
		this.z = z;
		this.dilation = dilation;
		this.blocks = new Block[WIDTH][WIDTH][HEIGHT];
		this.parent = parent;
	}

	/**
	 * Calculates visible sides of blocks and updates each block's mask accordingly
	 */
	public void calcVisible() {
		this.mask = true;
		
		for (int x = 0; x < WIDTH; x++) {
			for (int z = 0; z < WIDTH; z++) {
				for (int y = 0; y < HEIGHT; y++) {
					if (x == 0) {
						if (!parent.getChunk(this.x - 1, this.z, false).blocks[WIDTH - 1][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawLeft = true;
						}
						if (!blocks[x + 1][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawRight = true;
						}
					} else if (x == WIDTH - 1) {
						if (!parent.getChunk(this.x + 1, this.z, false).blocks[0][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawRight = true;	
						}
						if (!blocks[x - 1][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawLeft = true;
						}
					} else {
						if (!blocks[x + 1][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawRight = true;
						}

						if (!blocks[x - 1][z][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawLeft = true;
						}
					}

					if (z == 0) {
						if (!parent.getChunk(this.x, this.z - 1, false).blocks[x][WIDTH - 1][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawNear = true;
						}
						if (!blocks[x][z + 1][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawFar = true;
						}
					} else if (z == WIDTH - 1) {
						if (!parent.getChunk(this.x, this.z + 1, false).blocks[x][0][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawFar = true;	
						}
						if (!blocks[x][z - 1][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawNear = true;
						}
					} else {
						if (!blocks[x][z + 1][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawFar = true;
						}

						if (!blocks[x][z - 1][y].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawNear = true;
						}
					}

					if (y == HEIGHT - 1) {
						blocks[x][z][y].mask.render = true;
						blocks[x][z][y].mask.drawTop = true;
					} else if (y != 0) {
						if (!blocks[x][z][y + 1].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawTop = true;
						}

						if (!blocks[x][z][y - 1].visible()) {
							blocks[x][z][y].mask.render = true;
							blocks[x][z][y].mask.drawBottom = true;
						}	
					}
				}
			}
		}
	}

	/**
	 * Draws each block in the Chunk
	 */
	public void drawBlocks() {
		for (Block[][] a : blocks) {
			for (Block[] b : a) {
				for (Block block : b) {
					block.draw();
				}
			}
		}
	}
}
