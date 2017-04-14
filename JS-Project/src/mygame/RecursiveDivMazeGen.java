/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
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
    final int WALL_THICKNESS;
    int minRoomWidth;
    int minRoomHeight;
    int doorSize;
    boolean cutIsHorizontal;
    
    private boolean isCutHorizontal(float quadWidth, float quadHeight)
    {
        return quadHeight > quadWidth;
    }
    
    private int randomizeCarvePlacement(int min, int max)
    {
        //Returns a random number that will be the central pixel in the lenght of the wall that gets carved.
    }
    
    private void carveOpening(Geometry[] geos)
    {
        int doorCentre = randomizeCarvePlacement();
        //Delete previous object, create two objects that leave the opening. doorSize/2 for each side
    }
    
    private Geometry[] fillSection(Box[] boxesToFillWith, Vector3f sectionCentrePos)
    {
        Geometry[] geosToReturn = new Geometry[2];
        
        geosToReturn[0] = new Geometry("Box1", boxesToFillWith[0]);
        geosToReturn[1] = new Geometry("Box2", boxesToFillWith[1]);
        
        if(cutIsHorizontal)
        {
            geosToReturn[0].setLocalTranslation(sectionCentrePos.x, sectionCentrePos.y + boxesToFillWith[0].getYExtent()/2 
                    + WALL_THICKNESS, sectionCentrePos.z);
            geosToReturn[1].setLocalTranslation(sectionCentrePos.x, sectionCentrePos.y - boxesToFillWith[1].getYExtent()/2 
                    - WALL_THICKNESS, sectionCentrePos.z);
        }
        else
        {
            geosToReturn[0].setLocalTranslation(sectionCentrePos.x - boxesToFillWith[0].getXExtent()/2 - WALL_THICKNESS, 
                    sectionCentrePos.y, sectionCentrePos.z);
            geosToReturn[1].setLocalTranslation(sectionCentrePos.x + boxesToFillWith[1].getXExtent()/2 + WALL_THICKNESS, 
                    sectionCentrePos.y, sectionCentrePos.z);
        }
        return geosToReturn;
    }
    
    private Box[] bisectSection(Quad section)
    {
        //boxesToReturn[0] represents either the left or the up Quad, depending on the orientation of the bisection
        Box[] boxesToReturn = new Box[2];
        if(section.getWidth() > minRoomWidth && section.getHeight() > minRoomHeight)
        {
            cutIsHorizontal = isCutHorizontal(section.getWidth(), section.getHeight());
            if(cutIsHorizontal)
            {    
                boxesToReturn[0] = new Box(section.getWidth(), section.getHeight()/2 - WALL_THICKNESS, 1f);
            }
            else
            {
                boxesToReturn[0] = new Box(section.getWidth()/2 - WALL_THICKNESS, section.getHeight(), 1f);
            }
            boxesToReturn[1] = boxesToReturn[0];
            
            
        }
        
        return boxesToReturn;
    }
    
    private void recursiveDivision(Quad area)
    {
        if(area.getWidth() > minRoomWidth && area.getHeight() > minRoomHeight)
        {
            Box[] dividedBoxes = bisectSection(area);
            Geometry[] dividedGeos = fillSection(dividedBoxes, area.getBound().getCenter());
            carveOpening(dividedGeos);
            
            recursiveDivision(dividedBoxes[0]);
            recursiveDivision(dividedBoxes[1]);
        }
    }
    
    public void generateMaze()
    {
        /*Gives centre of Geometry
        plane.getLocalTransform().getTranslation();*/
        recursiveDivision(new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
    }
    
    //(0,0) will be on the bottom left
    private void givePlaneSaneCoordinates(Geometry plane)
    {
        Vector3f temp = plane.getLocalTranslation();
        temp.x = 0 + MAX_AREA_WIDTH/2;
        temp.y = 0 + MAX_AREA_HEIGHT/2;
        
        plane.setLocalTranslation(temp);
    }
    
    public RecursiveDivMazeGen(int areaWidth, int areaHeight, int newMinRoomWidth, int newMinRoomHeight, int doorSize, 
            int wallThickness)
    {
        MAX_AREA_WIDTH = areaWidth;
        MAX_AREA_HEIGHT = areaHeight;
        WALL_THICKNESS = wallThickness;
        minRoomWidth = newMinRoomWidth;
        minRoomHeight = newMinRoomHeight;
        plane = new Geometry("Floor", new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        givePlaneSaneCoordinates(plane);
        generatedMaze = new Node();
    }
}
