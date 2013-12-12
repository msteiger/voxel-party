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

import nexus.model.generators.Perlin;
import nexus.model.renderable.Air;
import nexus.model.renderable.Solid;

import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.generator.city.WorldFacade;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sectors;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class PerlinTerrainGenerator
{
	public static final int BIG_NUMBER = (int) Math.pow(2, 18);

	public void generateChunk(Chunk chunk)
	{
		for (int i = 0; i < Chunk.WIDTH; i++)
		{
			for (int j = 0; j < Chunk.WIDTH; j++)
			{
				Color color = new Color(0.2f, (float) (0.7f + Math.random() * 0.2f), 0.4f);

				float x = (float) chunk.x * Chunk.WIDTH + i;
				float z = (float) chunk.z * Chunk.WIDTH + j;
				float y = (int) chunk.dilation.y
						* ((Perlin.perlin2D(x * chunk.dilation.x + BIG_NUMBER, z * chunk.dilation.z + BIG_NUMBER) + 1) / 2)
						+ 1;

				for (int k = 0; k < Chunk.HEIGHT; k++)
				{
					if (k > y - 1 && k < y)
					{
						chunk.blocks[i][j][k] = new Solid(new Vector3(x, k, z), 1.0f, color);
					}
					else
					{
						chunk.blocks[i][j][k] = Air.INSTANCE;
					}
				}
			}
		}
	}
}
