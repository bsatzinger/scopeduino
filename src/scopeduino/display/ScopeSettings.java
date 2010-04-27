//ScopeSettings.java
//Used to store settings for the oscilloscope display

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License as
//published by the Free Software Foundation; either version 3 of the
//License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but
//WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//General Public License for more details:
//http://www.gnu.org/licenses/gpl.txt

//Copyright 2009 Brian Satzinger

package scopeduino.display;

/**
 *
 * @author brian
 */
public class ScopeSettings {
    static float backr, backg, backb, z;

    static boolean hardFirstTrace = false;

    static float amp;

    static float lineWidth = .01f;

    //vertical cursor locations
    static float hc1 = -1.0f;
    static float hc2 = -1.0f;

    static float vc1 = -1.0f;
    static float vc2 = -1.0f;

    static int vcChannel = 1;

    //enable grid
    static boolean grid = true;

    //vertical cursor enable
    static boolean enableCursors = false;

    //cursor line width
    static float cursorWidth = 1.5f;

    //cursor color
    static float hcursorR = 1.0f;
    static float hcursorG = 1.0f;
    static float hcursorB = 1.0f;

    static float vcursorR = 1.0f;
    static float vcursorG = 1.0f;
    static float vcursorB = 1.0f;

    //DAQ timing info
    static float samplePeriod = .000033333f;

    //Horizontal scale settings
    static int horizontalWindow = 768;
    static int horizontalOffset = 0;


    //Channel 1 Color
    static float ch1R = 0.2f;
    static float ch1G = 0.2f;
    static float ch1B = 1.0f;

    //Channel 2 Color
    static float ch2R = 1.0f;
    static float ch2G = 1.0f;
    static float ch2B = 0.2f;

    //Channel 1 Vertical Scale
    static float ch1MinV = 0.0f;
    static float ch1MaxV = 5.0f;

    //Channel 2 Vertical Scale
    static float ch2MinV = 0.0f;
    static float ch2MaxV = 5.0f;


    //Trigger level indicator
    static boolean enableTriggerIndicator = false;
    static float triggerIndicator = 0.1f;
    static float triggerWidth = 1.0f;
}
