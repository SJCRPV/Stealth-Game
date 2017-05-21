/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.MapGeneration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author SJCRPV
 */
public abstract class Generation {
    
    protected static Cell[][] grid;
    //completedAreas takes an int array of lenght 4 with the elements of minMaxWide first and the minMaxTall last.
    protected static List<int[]> completedAreas = new ArrayList<>();
    protected static float CELL_WIDTH;
    protected static float CELL_HEIGHT;
    protected static float WALL_THICKNESS;
    protected static int DOOR_SIZE;
    
    
    protected int generateRandomNum(int lowerBound, int higherBound)
    {
        return ThreadLocalRandom.current().nextInt(lowerBound, higherBound);
    }
    
    protected float calcSize(int numOfCells)
    {
        return ((WALL_THICKNESS + CELL_WIDTH * 2) * numOfCells);
    }
}
