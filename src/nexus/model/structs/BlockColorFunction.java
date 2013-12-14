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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.CoreRegistry;
import org.terasology.world.generator.city.BlockTypes;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * A mapping from block types (as defined in {@link BlockTypes}) to actual blocks
 * @author Martin Steiger
 */
final class BlockColorFunction implements Function<String, Color> {

    private static final Logger logger = LoggerFactory.getLogger(BlockColorFunction.class);

    private final Map<String, Color> map = Maps.newConcurrentMap();
    private final Color defaultColor = new Color(0.3f, 0.3f, 0.3f);

    /**
     * Setup the mapping with defaults 
     */
    public BlockColorFunction() {
        map.put(BlockTypes.ROAD_SURFACE, new Color(0.4f, 0.4f, 0.4f));
        map.put(BlockTypes.LOT_EMPTY, new Color(0.9f, 0.9f, 0.5f));
        map.put(BlockTypes.BUILDING_WALL, new Color(0.7f, 0.7f, 0.7f));
        map.put(BlockTypes.BUILDING_FLOOR, new Color(0.3f, 0.2f, 0.2f));
        map.put(BlockTypes.ROOF_FLAT, new Color(0.8f, 0.2f, 0.2f));
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

    	Color block = map.get(input);

        if (block == null) {
            block = defaultColor;
            logger.warn("Could not resolve block type \"{}\" - using default", input);
        }

        return block;
    }
}
