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

import java.util.Map;

import org.terasology.cities.BlockTypes;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * A mapping from block types (as defined in {@link BlockTypes}) to actual blocks
 * @author Martin Steiger
 */
final class BlockColorFunction implements Function<String, Color> {

    private final Map<String, Color> map = Maps.newHashMap();

    /**
     * Setup the mapping with defaults 
     */
    public BlockColorFunction() {
        map.put(BlockTypes.AIR, null);
        
        map.put(BlockTypes.ROAD_SURFACE, new Color(0.4f, 0.4f, 0.4f));
        map.put(BlockTypes.LOT_EMPTY, new Color(0.9f, 0.9f, 0.5f));
        map.put(BlockTypes.BUILDING_WALL, new Color(0.7f, 0.7f, 0.7f));
        map.put(BlockTypes.BUILDING_FLOOR, new Color(0.3f, 0.2f, 0.2f));
        map.put(BlockTypes.ROOF_FLAT, new Color(0.8f, 0.2f, 0.2f));
        map.put(BlockTypes.BUILDING_FOUNDATION, new Color(0.4f, 0.4f, 0.4f));
        map.put(BlockTypes.ROOF_HIP, new Color(0.6f, 0.2f, 0.2f));
        map.put(BlockTypes.ROOF_SADDLE, new Color(0.6f, 0.2f, 0.2f));
        map.put(BlockTypes.ROOF_DOME, new Color(0.6f, 0.6f, 0.5f));
        map.put(BlockTypes.ROOF_GABLE, new Color(0.5f, 0.3f, 0.3f));
        
    }

    /**
     * Remove blockType from the mapping
     * @param blockType the block type (as defined in BlockTypes} 
     */
    public void unregister(String blockType) {
        map.remove(blockType);
    }

    @Override
    public Color apply(String input) {

    	if (!map.containsKey(input)) {
    		System.out.println("BlockColorFunction does not know " + input);
    		return new Color(0.5f, 0.5f, 0.5f);
    	}
    	
    	Color block = map.get(input);

        return block;
    }
}