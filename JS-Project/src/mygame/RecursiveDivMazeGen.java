/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author SJCRPV
 */
public class RecursiveDivMazeGen {
    
    Node generatedMaze;
    Geometry plane;
    final int MAX_AREA_WIDTH;
    final int MAX_AREA_HEIGHT;
    int minRoomWidth;
    int minRoomHeight;
    int doorWidth;
    int doorHeight;
    
    private void carveOpening(boolean isHorizontal, int[] bisectionStartPos, int bisectionSize)
    {
        //Randomize placement of opening.
        //Delete previous object, create two objects that leave the opening
    }
    
    private Quad[] bisectSection(Vector3f sectionCentrePos, Quad section)
    {
        //quadsToReturn[0] represents either the left or the up Quad, depending on the orientation of the bisection
        Quad[] quadsToReturn = new Quad[2];
        if(section.getWidth() > minRoomWidth && section.getHeight() > minRoomHeight)
        {
            //See if bisection will be vertical of horizontal
            //Make bisection
            //Make object to fill bisection
            //carveOpening();
        }
        
        return quadsToReturn;
    }
    
    private void recursiveDivision(Quad area)
    {
        
        if(area.getWidth() > minRoomWidth && area.getHeight() > minRoomHeight)
        {
            Quad[] dividedQuads = bisectSection(area.getBound().getCenter(), area);
            
            recursiveDivision(dividedQuads[0]);
            recursiveDivision(dividedQuads[1]);
        }
    }
    
    public void generateMaze()
    {
        //Gives centre of Geometry
        //plane.getLocalTransform().getTranslation();
        recursiveDivision(new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
    }
    
    public RecursiveDivMazeGen(int areaWidth, int areaHeight, int newMinRoomWidth, int newMinRoomHeight, int newDoorWidth, int newDoorHeight)
    {
        MAX_AREA_WIDTH = areaWidth;
        MAX_AREA_HEIGHT = areaHeight;
        minRoomWidth = newMinRoomWidth;
        minRoomHeight = newMinRoomHeight;
        plane = new Geometry("Floor", new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        generatedMaze = new Node();
    }
}
