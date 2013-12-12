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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import org.terasology.common.CachingFunction;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generator.city.BlockTypes;
import org.terasology.world.generator.city.WorldFacade;
import org.terasology.world.generator.city.model.Building;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.RectBuilding;
import org.terasology.world.generator.city.model.RectLot;
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
    private Function<Vector2i, Integer> heightMap = CachingFunction.wrap(new Function<Vector2i, Integer>() {

    	private final Random random = new FastRandom("asdf".hashCode());
    	
		@Override
		public Integer apply(Vector2i input)
		{
			return 1 + random.nextInt(1);
		}
    	
    }); 

    /**
	 * 
	 */
	public CityTerrainGenerator()
	{
		facade = new WorldFacade("a");
	}

	public void generateChunk(Chunk chunk) {
        
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;
        int sx = (int) Math.floor((double) wx / Sector.SIZE);
        int sz = (int) Math.floor((double) wz / Sector.SIZE);

        for (int x = 0; x < Chunk.WIDTH; x++) {
        	for (int z = 0; z < Chunk.WIDTH; z++) {
            	for (int y = 0; y < Chunk.HEIGHT; y++) {
            		chunk.blocks[x][z][y] = Air.INSTANCE;
            	}

            	int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                Color grass = new Color(0.5f, 0.8f, 0.4f);
        		setBlock(chunk, x, y, z, grass);
        	}
        }
        
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        drawRoads(sector, chunk);
        drawCities(sector, chunk);
    }
    
    private void drawRoads(Sector sector, Chunk chunk) {
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, Chunk.WIDTH, Chunk.WIDTH);
        Shape roadArea = facade.getRoadArea(sector);

        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        for (int z = 0; z < Chunk.WIDTH; z++) {
            for (int x = 0; x < Chunk.WIDTH; x++) {
            	
            	Color block;
                if (roadArea.contains(wx + x, wz + z)) {
                    block = blockType.apply(BlockTypes.ROAD_SURFACE); 

                    int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                    setBlock(chunk, x, y, z, block);
                 }
            }
        }
    }
    
    private void drawCities(Sector sector, Chunk chunk) {
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, Chunk.WIDTH, Chunk.WIDTH);
        
        Set<City> cities = facade.getCities(sector);
        
        for (City city : cities) {
        	Set<RectLot> lots = facade.getLots(city);
        	
        	for (RectLot lot : lots) {
        		if (lot.getShape().intersects(chunkRect)) {
        			rasterLot(chunk, lot);
        		}
        	}
        }
    }
    
    /**
	 * @param lot
	 */
	private void rasterLot(Chunk chunk, RectLot lot)
	{
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;
        
        Rectangle rc = lot.getShape();
        
        Color color = blockType.apply(BlockTypes.LOT_EMPTY);
        
		for (int z = rc.y; z < rc.y + rc.height; z++) {
        	for (int x = rc.x; x < rc.x + rc.width; x++) {
        		if ((x >= wx && x < wx + Chunk.WIDTH) &&
        			(z >= wz && z < wz + Chunk.WIDTH)) {
        			int y = heightMap.apply(new Vector2i(x, z));
        			setBlock(chunk, x - wx, y, z - wz, color);
        		}
        	}
        }
		
		for (Building blg : lot.getBuildings()) {
			RectBuilding rblg = (RectBuilding) blg;			// HACK
			
			rasterBuilding(chunk, rblg);
		}
	}

	private void rasterBuilding(Chunk chunk, RectBuilding blg)
	{
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;

        Rectangle rc = blg.getLayout();
        
        // HACK: make sure area is flat
        
        Color color = blockType.apply(BlockTypes.BUILDING_WALL);
        
        // wall along z
		for (int z = rc.y; z < rc.y + rc.height; z++) {
        	for (int x = rc.x; x < rc.x + rc.width; x+=rc.width-1) {
        		if ((x >= wx && x < wx + Chunk.WIDTH) &&
          			(z >= wz && z < wz + Chunk.WIDTH)) {
           			int y = heightMap.apply(new Vector2i(x, z));
           			
           			for (int h = 0; h < blg.getHeight(); h++) {
           				setBlock(chunk, x - wx, y + h, z - wz, color);
           			}
        		}
        	}
		}

	    color = blockType.apply(BlockTypes.BUILDING_WALL);
	       
	    // wall along x
		for (int z = rc.y; z < rc.y + rc.height; z += rc.height - 1) {
			for (int x = rc.x; x < rc.x + rc.width; x++) {
        		if ((x >= wx && x < wx + Chunk.WIDTH) &&
          			(z >= wz && z < wz + Chunk.WIDTH)) {
           			int y = heightMap.apply(new Vector2i(x, z));
           			
           			for (int h = 0; h < blg.getHeight(); h++) {
           				setBlock(chunk, x - wx, y + h, z - wz, color);
           			}
        		}
        	}
		}

	    color = blockType.apply(BlockTypes.ROOF_FLAT);
	       
		// roof
		for (int z = rc.y; z < rc.y + rc.height; z++) {
        	for (int x = rc.x; x < rc.x + rc.width; x++) {
        		if ((x >= wx && x < wx + Chunk.WIDTH) &&
          			(z >= wz && z < wz + Chunk.WIDTH)) {
           			int y = heightMap.apply(new Vector2i(x, z)) + blg.getHeight();
           			
           			setBlock(chunk, x - wx, y, z - wz, color);
        		}
        	}
		}

	}

	private void setBlock(Chunk chunk, int x, int y, int z, Color color) {
        int wx = chunk.x * Chunk.WIDTH;
        int wz = chunk.z * Chunk.WIDTH;

        Solid solid = new Solid(new Vector3(wx + x, y, wz + z), 1.0f, color);
        chunk.blocks[x][z][y] = solid;
    }
}
