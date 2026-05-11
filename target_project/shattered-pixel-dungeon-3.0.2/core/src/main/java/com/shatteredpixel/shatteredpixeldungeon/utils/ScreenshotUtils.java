/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.watabou.utils.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScreenshotUtils {

	private static final String SCREENSHOT_DIR = "screenshots/";
	private static final String FILE_PREFIX = "spd_screenshot_";
	private static final SimpleDateFormat FILE_TIMESTAMP =
			new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.ENGLISH);

	public static void captureCurrentFrame() {
		Pixmap pixmap = null;
		try {
			int width = Gdx.graphics.getBackBufferWidth();
			int height = Gdx.graphics.getBackBufferHeight();
			byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, width, height, true);

			pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
			BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

			FileHandle dir = FileUtils.getFileHandle(SCREENSHOT_DIR);
			dir.mkdirs();

			FileHandle file = FileUtils.getFileHandle(
					SCREENSHOT_DIR + FILE_PREFIX + FILE_TIMESTAMP.format(new Date()) + ".png");
			PixmapIO.writePNG(file, pixmap);
			GLog.p("Screenshot saved: " + file.path());
		} catch (Exception e) {
			ShatteredPixelDungeon.reportException(e);
			GLog.w("Screenshot failed.");
		} finally {
			if (pixmap != null) {
				pixmap.dispose();
			}
		}
	}

	private ScreenshotUtils() {
	}
}
