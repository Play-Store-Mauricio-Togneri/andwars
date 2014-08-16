package com.mauriciotogneri.andwars.util;

import android.graphics.Color;

public class ColorUtils
{
	private static final int COLOR_DIFF = 50;

	public static int getDarkColor(int color)
	{
		int diff = ColorUtils.COLOR_DIFF;
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		
		return Color.argb(255, (red >= diff) ? (red - diff) : red, (green >= diff) ? (green - diff) : green, (blue >= diff) ? (blue - diff) : blue);
	}
}