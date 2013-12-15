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

import nexus.model.raster.RasterRegistration;
import nexus.model.raster.Rasterizer;
import nexus.model.raster.ReflectionRegistrar;
import nexus.model.raster.standard.Dummy;
import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.generator.city.BlockTypes;
import org.terasology.world.generator.city.WorldFacade;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.HipRoof;
import org.terasology.world.generator.city.model.Roof;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sector.Orientation;
import org.terasology.world.generator.city.model.Sectors;
import org.terasology.world.generator.city.model.SimpleBuilding;
import org.terasology.world.generator.city.model.SimpleLot;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityTerrainGenerator {

    private WorldFacade facade;
    private BlockColorFunction blockType = new BlockColorFunction();
    private RasterRegistration rasterizer = new RasterRegistration();
    private VoxelBrush brush = new VoxelBrush(blockType);

    /**
	 * 
	 */
	public CityTerrainGenerator()
	{
		facade = new WorldFacade("a");
		
		ReflectionRegistrar rr = new ReflectionRegistrar(rasterizer);
		rr.registerPackageOfClass(Dummy.class);
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
        
        int baseHeight = blg.getBaseHeight() + 1;	// start 1 block above terrain
        int wallHeight = blg.getWallHeight();

        brush.clearAbove(chunk, rc, baseHeight);
        
        brush.fill(chunk, rc, baseHeight - 1, baseHeight, BlockTypes.BUILDING_FLOOR);
        brush.fillAirBelow(chunk, rc, baseHeight - 2, BlockTypes.BUILDING_FLOOR);
        
        // wall along z
        brush.createWallZ(chunk, rc.y, rc.y + rc.height, rc.x, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
        brush.createWallZ(chunk, rc.y, rc.y + rc.height, rc.x + rc.width - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);

        // wall along x
        brush.createWallX(chunk, rc.x, rc.x + rc.width, rc.y, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
        brush.createWallX(chunk, rc.x, rc.x + rc.width, rc.y + rc.height - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);

        // door
        Rectangle door = blg.getDoor();
        Vector3i doorFrom = new Vector3i(door.x, baseHeight, door.y);
		Vector3i doorTo = new Vector3i(door.x + door.width, baseHeight + blg.getDoorHeight(), door.y + door.height);
		brush.fill(chunk, doorFrom, doorTo, null);
        
		rasterize(chunk, blg.getRoof());
	}

	private <T> void rasterize(Chunk chunk, T obj) {
		Optional<Rasterizer<T>> opt = rasterizer.getRasterizer(obj);
		if (opt.isPresent()) {
			Rasterizer<T> r = opt.get();
			r.raster(chunk, brush, obj);
		}
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
