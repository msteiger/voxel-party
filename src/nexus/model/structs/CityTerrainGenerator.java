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
import java.util.Set;

import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.generator.city.BlockTypes;
import org.terasology.world.generator.city.WorldFacade;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sector.Orientation;
import org.terasology.world.generator.city.model.Sectors;
import org.terasology.world.generator.city.model.SimpleBuilding;
import org.terasology.world.generator.city.model.SimpleLot;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityTerrainGenerator {

    private WorldFacade facade;
    private BlockColorFunction blockType = new BlockColorFunction();

    /**
	 * 
	 */
	public CityTerrainGenerator()
	{
		facade = new WorldFacade("a");
	}

	public void generateChunk(Chunk chunk) {
        
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();
        int sx = (int) Math.floor((double) wx / Sector.SIZE);
        int sz = (int) Math.floor((double) wz / Sector.SIZE);

        for (int x = 0; x < chunk.getChunkSizeX(); x++) {
        	for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
                Color grass = new Color(0.5f, 0.8f, 0.4f);
                Color chunkBorder = new Color(0.5f, 0.4f, 0.9f);
                Color sectorBorder = new Color(0.1f, 0.1f, 0.1f);
                Color color;
                
               	color = grass;

               	if (x == 0 || z == 0) 
                	color = chunkBorder;
                
                if (x == 0 && (wx % Sector.SIZE == 0))
                	color = sectorBorder; 
                if (z == 0 && (wz % Sector.SIZE == 0))
                	color = sectorBorder;
        		
            	int y0 = facade.getHeightMap().apply(new Vector2i(wx + x, wz + z));
            	for (int y = 0; y <= y0; y++) {
            		setBlock(chunk, x, y, z, color);
            	}
            	for (int y = y0 + 1; y < Chunk.HEIGHT; y++) {
            		setBlock(chunk, x, y, z, null);
            	}
        	}
        }
        
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        drawRoads(sector, chunk);
        drawCities(sector, chunk);
    }
    
    private void drawRoads(Sector sector, Chunk chunk) {
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
        Shape roadArea = facade.getRoadArea(sector);

        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        Function<Vector2i, Integer> heightMap = facade.getHeightMap();
        
        for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
            for (int x = 0; x < chunk.getChunkSizeX(); x++) {
            	
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
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
        
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));
        
        for (Orientation dir : Orientation.values()) {
        	cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }
        
        for (City city : cities) {
        	Set<SimpleLot> lots = facade.getLots(city);
        	
        	for (SimpleLot lot : lots) {
        		if (lot.getShape().intersects(chunkRect)) {
        			rasterLot(chunk, lot);
        		}
        	}
        }
    }
    
	private void rasterLot(Chunk chunk, SimpleLot lot)
	{
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();
        
        Rectangle rc = lot.getShape();
        
        Color color = blockType.apply(BlockTypes.LOT_EMPTY);
        
        Function<Vector2i, Integer> heightMap = facade.getHeightMap();

		for (int z = rc.y; z < rc.y + rc.height; z++) {
        	for (int x = rc.x; x < rc.x + rc.width; x++) {
        		if ((x >= wx && x < wx + chunk.getChunkSizeX()) &&
        			(z >= wz && z < wz + chunk.getChunkSizeZ())) {
        			int y = heightMap.apply(new Vector2i(x, z));
       				setBlock(chunk, x - wx, y, z - wz, color);
        		}
        	}
        }
		
		for (SimpleBuilding blg : lot.getBuildings()) {
			
			rasterBuilding(chunk, blg);
		}
	}

	private void rasterBuilding(Chunk chunk, SimpleBuilding blg)
	{
        Rectangle rc = blg.getLayout();
        Color color;
        
        int baseHeight = blg.getBaseHeight() + 1;	// start 1 block above terrain
        int wallHeight = blg.getWallHeight();

        clearAbove(chunk, rc, baseHeight);
        
        fillRect(chunk, rc, baseHeight - 1, baseHeight, BlockTypes.BUILDING_FLOOR);
        fillAirBelow(chunk, rc, baseHeight - 2, BlockTypes.BUILDING_FLOOR);
        
        color = blockType.apply(BlockTypes.BUILDING_WALL);
        
		createWallZ(chunk, rc.y, rc.y + rc.height, rc.x, baseHeight, wallHeight, color);
        createWallZ(chunk, rc.y, rc.y + rc.height, rc.x + rc.width - 1, baseHeight, wallHeight, color);

        color = blockType.apply(BlockTypes.BUILDING_WALL);
	       
	    // wall along x
        createWallX(chunk, rc.x, rc.x + rc.width, rc.y, baseHeight, wallHeight, color);
        createWallX(chunk, rc.x, rc.x + rc.width, rc.y + rc.height - 1, baseHeight, wallHeight, color);

        // door
        Rectangle door = blg.getDoor();
        Vector3i doorFrom = new Vector3i(door.x, baseHeight, door.y);
		Vector3i doorTo = new Vector3i(door.x + door.width, baseHeight + 2, door.y + door.height);
		fill(chunk, doorFrom, doorTo, null);
        
        // roof
	    createRoof(chunk, rc, baseHeight + wallHeight);

	}

	private void createRoof(Chunk chunk, Rectangle org, int h0)
	{
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();
        
        Rectangle cur = new Rectangle(org.x - 1, org.y - 1, org.width + 2, org.height + 2);

        int invSlope = 1;
        int maxInc = Math.min(cur.width, cur.height) / (2 * invSlope);	// this is the ground truth
        maxInc = 1;

	    Color color = blockType.apply(BlockTypes.ROOF_FLAT);

		// roof
		for (int z = cur.y; z < cur.y + cur.height; z++) {
        	for (int x = cur.x; x < cur.x + cur.width; x++) {
        		if ((x >= wx && x < wx + Chunk.WIDTH) &&
          			(z >= wz && z < wz + Chunk.WIDTH)) {

        			int rx = x - cur.x;
        			int rz = z - cur.y;
        			
        			// distance to border of the roof
        			int borderDistX = Math.min(rx, cur.width - 1 - rx);
        			int borderDistZ = Math.min(rz, cur.height - 1 - rz);
        			
        			int dist = Math.min(borderDistX, borderDistZ);
        			
        			int inc = Math.min(maxInc, dist / invSlope);
					int y = h0 + inc;
        			
           			setBlock(chunk, x - wx, y, z - wz, color);
        		}
        	}
		}
		
	}

	/**
	 * @param chunk the chunk
	 * @param rc the area to clear
	 * @param y1 the base height to start
	 */
	private void clearAbove(Chunk chunk, Rectangle rc, int y1)
	{
		int x1 = rc.x;
		int x2 = rc.x + rc.width;
		int z1 = rc.y;
		int z2 = rc.y + rc.height;
		
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

		int minX = Math.max(x1, wx);
		int maxX = Math.min(x2, wx + chunk.getChunkSizeX());

		int minZ = Math.max(z1, wz);
		int maxZ = Math.min(z2, wz + chunk.getChunkSizeZ());

		for (int z = minZ; z < maxZ; z++) {
			for (int x = minX; x < maxX; x++) {
				
				// starting from the bottom, we go up until we hit air
				for (int y = y1; y < chunk.getChunkSizeY(); y++) {
					Block block = getBlock(chunk, x - wx, y, z - wz);
					if (block == Air.INSTANCE)
						break;
					
					setBlock(chunk, x - wx, y, z - wz, null);
				}
			}
		}		
	}

	/**
	 * @param chunk the chunk
	 * @param rc the area to clear
	 * @param y1 the base height to start
	 * @param type the block type that is used for filling
	 */
	private void fillAirBelow(Chunk chunk, Rectangle rc, int y1, String type)
	{
		int x1 = rc.x;
		int x2 = rc.x + rc.width;
		int z1 = rc.y;
		int z2 = rc.y + rc.height;
		
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

		int minX = Math.max(x1, wx);
		int maxX = Math.min(x2, wx + chunk.getChunkSizeX());

		int minZ = Math.max(z1, wz);
		int maxZ = Math.min(z2, wz + chunk.getChunkSizeZ());
		
		Color color = blockType.apply(type);

		for (int z = minZ; z < maxZ; z++) {
			for (int x = minX; x < maxX; x++) {
				
				// starting from the top, we go down until we have air
				for (int y = y1; y >= 0; y--) {
					Block block = getBlock(chunk, x - wx, y, z - wz);
					if (block != Air.INSTANCE)
						break;
					
					setBlock(chunk, x - wx, y, z - wz, color);
				}
			}
		}		
	}

	private void fillRect(Chunk chunk, Rectangle rc, int y1, int y2, String type)
	{
		Color color = blockType.apply(type);
		fill(chunk, new Vector3i(rc.x, y1, rc.y), new Vector3i(rc.x + rc.width, y2, rc.y + rc.height), color);		
	}
	
	private void createWallX(Chunk chunk, int x1, int x2, int z, int baseHeight, int height, Color color)
	{
		fill(chunk, new Vector3i(x1, baseHeight, z), new Vector3i(x2, baseHeight + height, z + 1), color);
	}
	
	private void createWallZ(Chunk chunk, int z1, int z2, int x, int baseHeight, int height, Color color)
	{
		fill(chunk, new Vector3i(x, baseHeight, z1), new Vector3i(x + 1, baseHeight + height, z2), color);
	}
	
	private void fill(Chunk chunk, Vector3i from, Vector3i to, Color color)
	{
		int x1 = from.x;
		int x2 = to.x;
		int y1 = from.y;
		int y2 = to.y;
		int z1 = from.z;
		int z2 = to.z;
		
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

		int minX = Math.max(x1, wx);
		int maxX = Math.min(x2, wx + chunk.getChunkSizeX());

		int minZ = Math.max(z1, wz);
		int maxZ = Math.min(z2, wz + chunk.getChunkSizeZ());

		for (int z = minZ; z < maxZ; z++) {
			for (int x = minX; x < maxX; x++) {
				for (int y = y1; y < y2; y++) {
					setBlock(chunk, x - wx, y, z - wz, color);
				}
			}
		}
	}

	private Block getBlock(Chunk chunk, int x, int y, int z) {
		return chunk.blocks[x][z][y];
	}

	private void setBlock(Chunk chunk, int x, int y, int z, Color color) {
        int wx = chunk.x * chunk.getChunkSizeX();
        int wz = chunk.z * chunk.getChunkSizeZ();

        Block block;
        if (color == null) {
        	block = Air.INSTANCE;
        } else {
        	block = new Solid(new Vector3(wx + x, y, wz + z), 1.0f, color);
        }
        chunk.blocks[x][z][y] = block;
    }
}
