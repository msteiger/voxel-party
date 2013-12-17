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

import org.terasology.cities.BlockTypes;
import org.terasology.cities.WorldFacade;
import org.terasology.cities.model.City;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.Roof;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.Sector.Orientation;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.ReflectionRegistrar;
import org.terasology.cities.raster.standard.CityRasterizer;
import org.terasology.cities.raster.standard.Dummy;
import org.terasology.cities.raster.standard.RoadRasterizer;
import org.terasology.cities.raster.standard.SimpleLotRasterizer;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.NoiseHeightMap;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;

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
    private RasterRegistry rasterizer = new RasterRegistry();
	private HeightMap heightMap;

    /**
	 * 
	 */
	public CityTerrainGenerator()
	{
		heightMap = new NoiseHeightMap();
		facade = new WorldFacade("a", heightMap);
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
        		
            	int y0 = heightMap.apply(new Vector2i(wx + x, wz + z));
            	for (int y = 0; y <= y0; y++) {
            		setBlock(chunk, x, y, z, color);
            	}
            	for (int y = y0 + 1; y < Chunk.HEIGHT; y++) {
            		setBlock(chunk, x, y, z, null);
            	}
        	}
        }
        
        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
	       
		VoxelBrush brush= new VoxelBrush(chunk, heightMap, blockType);

        drawCities(sector, brush);
        drawRoads(sector, brush);
    }
    
    private void drawRoads(Sector sector, Brush brush) {
        Shape roadArea = facade.getRoadArea(sector);
        
        RoadRasterizer rr = new RoadRasterizer();
        rr.draw(brush, roadArea);
    }

    private void drawCities(Sector sector, Brush brush) {
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));
        
        for (Orientation dir : Orientation.values()) {
            cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }
        
        CityRasterizer cr = new CityRasterizer();
        
        for (City city : cities) {
            cr.raster(brush, city);
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
