/*
 * Copyright (C) 2012-2013 Martin Steiger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nexus.model.structs;

import nexus.model.raster.AbstractBrush;
import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import com.google.common.base.Function;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class VoxelBrush extends AbstractBrush
{
	private final Function<String, Color> blockType;

	/**
	 * @param blockType
	 */
	public VoxelBrush(Function<String, Color> blockType)
	{
		this.blockType = blockType;
	}
	

	@Override
	public boolean isAir(Chunk chunk, int x, int y, int z) {
		return chunk.blocks[x][z][y] == Air.INSTANCE;
	}
	
	@Override
	public void setBlock(Chunk chunk, int x, int y, int z, String type) {
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

        Color color = blockType.apply(type);
        
        Block block;
        if (color == null) {
        	block = Air.INSTANCE;
        } else {
        	block = new Solid(new Vector3(wx + x, y, wz + z), 1.0f, color);
        }
        
        chunk.blocks[x][z][y] = block;
    }
}
