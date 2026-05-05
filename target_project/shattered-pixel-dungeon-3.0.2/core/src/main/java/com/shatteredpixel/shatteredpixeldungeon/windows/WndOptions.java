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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class WndOptions extends Window {

	protected static final int WIDTH_P = 120;
	protected static final int WIDTH_L = 144;

	protected static final int MARGIN 		= 2;
	protected static final int BUTTON_HEIGHT	= 18;

	public WndOptions(Image icon, String title, String message, String... options) {
		super();

		int width = windowWidth();

		float pos = 0;
		if (title != null) {
			IconTitle tfTitle = new IconTitle(icon, title);
			tfTitle.setRect(0, pos, width, 0);
			add(tfTitle);

			pos = tfTitle.bottom() + 2*MARGIN;
		}

		layoutBody(pos, message, options);
	}
	
	public WndOptions( String title, String message, String... options ) {
		super();

		int width = windowWidth();

		float pos = MARGIN;
		if (title != null) {
			RenderedTextBlock tfTitle = PixelScene.renderTextBlock(title, 9);
			tfTitle.hardlight(TITLE_COLOR);
			tfTitle.setPos(MARGIN, pos);
			tfTitle.maxWidth(width - MARGIN * 2);
			add(tfTitle);

			pos = tfTitle.bottom() + 2*MARGIN;
		}
		
		layoutBody(pos, message, options);
	}

	protected void layoutBody(float pos, String message, String... options){
		int width = windowWidth();
		pos = addMessageText(pos, message, width);
		pos = layoutButtons(pos, width, createOptionButtons(options));
		resize( width, (int)(pos - MARGIN) );
	}

	protected int windowWidth() {
		return PixelScene.landscape() ? WIDTH_L : WIDTH_P;
	}

	protected float addMessageText(float pos, String message, int width) {
		RenderedTextBlock tfMessage = PixelScene.renderTextBlock( 6 );
		tfMessage.text(message, width);
		tfMessage.setPos( 0, pos );
		add( tfMessage );
		return tfMessage.bottom() + 2*MARGIN;
	}

	protected ArrayList<RedButton> createOptionButtons(String... options) {
		ArrayList<RedButton> buttons = new ArrayList<>();

		for (int i = 0; i < options.length; i++) {
			buttons.add(createOptionButton(i, options[i]));
		}

		return buttons;
	}

	protected RedButton createOptionButton(final int index, String label) {
		RedButton btn = new RedButton( label ) {
			@Override
			protected void onClick() {
				hide();
				onSelect( index );
			}
		};
		if (hasIcon(index)) btn.icon(getIcon(index));
		btn.enable(enabled(index));
		add(btn);
		return btn;
	}

	protected float layoutButtons(float pos, int width, ArrayList<RedButton> buttons) {
		for (int i = 0; i < buttons.size(); i++) {
			RedButton btn = buttons.get(i);
			if (!hasInfo(i)) {
				btn.setRect(0, pos, width, BUTTON_HEIGHT);
			} else {
				btn.setRect(0, pos, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
				IconButton info = createInfoButton(i);
				info.setRect(width-BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
				add(info);
			}
			pos += BUTTON_HEIGHT + MARGIN;
		}
		return pos;
	}

	protected IconButton createInfoButton(final int index) {
		return new IconButton(Icons.get(Icons.INFO)){
			@Override
			protected void onClick() {
				onInfo( index );
			}
		};
	}

	protected boolean enabled( int index ){
		return true;
	}
	
	protected void onSelect( int index ) {}

	protected boolean hasInfo( int index ) {
		return false;
	}

	protected void onInfo( int index ) {}

	protected boolean hasIcon( int index ) {
		return false;
	}

	protected Image getIcon( int index ) {
		return null;
	}
}
