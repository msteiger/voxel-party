/*
 * Copyright 2013 MovingBlocks
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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Map;

import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.generator.city.BlockTypes;
import org.terasology.world.generator.city.WorldFacade;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sectors;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityTerrainGenerator {

    private WorldFacade facade;
    private BlockColorFunction blockType = new BlockColorFunction();
    private Function<? super Vector2i, Integer> heightMap = Functions.constant(10); 

    /**
	 * 
	 */
	public CityTerrainGenerator()
	{
		facade = new WorldFacade("a");
	}

	public void generateChunk(Chunk chunk) {
        if (facade == null) {
            throw new IllegalStateException("seed has not been set");
        }
        
        writeChunk(chunk);
    }

    private void writeChunk(Chunk chunk) {
        
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;
        int sx = (int) Math.floor((double) wx / Sector.SIZE);
        int sz = (int) Math.floor((double) wz / Sector.SIZE);
        int chunkSizeX = Chunk.WIDTH;
        int chunkSizeZ = Chunk.WIDTH;

        for (int x = 0; x < Chunk.WIDTH; x++) {
        	for (int z = 0; z < Chunk.WIDTH; z++) {
            	for (int y = 0; y < Chunk.HEIGHT; y++) {
            		chunk.blocks[x][z][y] = Air.INSTANCE;
            	}

            	int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                Color grass = new Color(0.5f, 0.8f, 0.4f);
        		chunk.blocks[x][z][y] = new Solid(new Vector3(wx + x, y, wz + z), 1.0f, grass);
        	}
        }
        
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunkSizeX, chunkSizeZ);
        Shape roadArea = facade.getRoadArea(sector);

        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        for (int z = 0; z < chunkSizeZ; z++) {
            for (int x = 0; x < chunkSizeX; x++) {
            	
            	Color block;
                if (roadArea.contains(wx + x, wz + z)) {
                    block = blockType.apply(BlockTypes.ROAD_SURFACE); 

                    int y = heightMap.apply(new Vector2i(wx + x, wz + z));

                    Solid solid = new Solid(new Vector3(wx + x, y, wz + z), 1.0f, block);
					chunk.blocks[x][z][y] = solid;
                }
            }
        }
    }
}
