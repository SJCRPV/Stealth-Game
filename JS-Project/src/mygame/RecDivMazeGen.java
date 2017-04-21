/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 *
 * @author SJCRPV
 */
public class RecDivMazeGen {
    
    Node generatedMaze;
    AssetManager assetManager;
    Geometry plane;
    Material mat;
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
    
    private Box[] carveOpening(Box box)
    {
        Box[] boxesToReturn = new Box[2];
        
        if(cutIsHorizontal)
        {
            boxesToReturn[0] = new Box(box.xExtent/2 - doorSize/2, box.yExtent/2, 1);
        }
        else
        {
            boxesToReturn[0] = new Box(box.xExtent/2, box.yExtent/2 - doorSize/2, 1);
        }
        
        boxesToReturn[1] = boxesToReturn[0];
        
        return boxesToReturn;
    }
    
    private Geometry[] bisectArea(Quad area)
    {
        Box bisection;
        Geometry[] bisectionGeoms = new Geometry[2];
        Vector3f centreOfBisection = new Vector3f(area.getBound().getCenter());
        Vector3f offsetAfterCarve;
        cutIsHorizontal = isCutHorizontal(area.getWidth(), area.getHeight());
        
        if(cutIsHorizontal)
        {
            bisection = new Box(area.getWidth(), WALL_THICKNESS, 1);
        }
        else
        {
            bisection = new Box(WALL_THICKNESS, area.getHeight(), 1);
        }
        
        Box[] carvedBoxes = carveOpening(bisection);
        
        if(cutIsHorizontal)
        {
            offsetAfterCarve = new Vector3f(centreOfBisection.x - carvedBoxes[0].xExtent/2, 0, 0);
        }
        else
        {
            offsetAfterCarve = new Vector3f(centreOfBisection.y - carvedBoxes[0].yExtent/2, 0, 0);
        }
        
        bisectionGeoms[0] = new Geometry("BisectionLeftOrUp", carvedBoxes[0]);
        bisectionGeoms[0].setLocalTranslation(centreOfBisection.subtract(offsetAfterCarve));
        bisectionGeoms[1] = new Geometry("BisectionRightOrDown", carvedBoxes[1]);
        bisectionGeoms[1].setLocalTranslation(centreOfBisection.add(offsetAfterCarve));
        
        return bisectionGeoms;
    }
    
    private void recursiveDivision(Quad area)
    {
        if(area.getWidth() > minRoomWidth && area.getHeight() > minRoomHeight)
        {
            //Bisect,
            //Fill,
            //Carve,
            //Recursion.
            Geometry[] bisectionGeoms = bisectArea(area);
            generatedMaze.attachChild(bisectionGeoms[0]);
            generatedMaze.attachChild(bisectionGeoms[1]);
            
            Box tempBox = (Box)bisectionGeoms[0].getMesh();
            Quad newArea = new Quad(tempBox.xExtent, tempBox.yExtent);
            recursiveDivision(newArea);
            
            tempBox = (Box)bisectionGeoms[1].getMesh();
            newArea = new Quad(tempBox.xExtent, tempBox.yExtent);
            recursiveDivision(newArea);
        }
    }
    
    public Node generateMaze()
    {
        recursiveDivision(new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        return generatedMaze;
    }
    
    private void createBorderWalls()
    {        
        Box up = new Box(MAX_AREA_WIDTH, WALL_THICKNESS, 1);
        Box left = new Box(WALL_THICKNESS, MAX_AREA_HEIGHT, 1);
        Box down = new Box(MAX_AREA_WIDTH, WALL_THICKNESS, 1);
        Box right = new Box(WALL_THICKNESS, MAX_AREA_HEIGHT, 1);
        
        Geometry upGeom = new Geometry("UpBorder", up);
        Geometry leftGeom = new Geometry("LeftBorder", left);
        Geometry downGeom = new Geometry("DownBorder", down);
        Geometry rightGeom = new Geometry("RightBorder", right);
        
        upGeom.setMaterial(mat);
        leftGeom.setMaterial(mat);
        downGeom.setMaterial(mat);
        rightGeom.setMaterial(mat);
        
        upGeom.setLocalTranslation(MAX_AREA_WIDTH/2, MAX_AREA_HEIGHT - WALL_THICKNESS, 0);
        leftGeom.setLocalTranslation(WALL_THICKNESS, MAX_AREA_HEIGHT/2, 0);
        downGeom.setLocalTranslation(MAX_AREA_WIDTH/2, WALL_THICKNESS, 0);
        rightGeom.setLocalTranslation(MAX_AREA_WIDTH - WALL_THICKNESS, MAX_AREA_HEIGHT/2, 0);
        
        generatedMaze.attachChild(upGeom);
        generatedMaze.attachChild(leftGeom);
        generatedMaze.attachChild(downGeom);
        generatedMaze.attachChild(rightGeom);
    }
    
    //(0,0) will be on the bottom left
    private void givePlaneSaneCoordinates()
    {
        Vector3f temp = plane.getLocalTranslation();
        temp.x = 0 + MAX_AREA_WIDTH/2;
        temp.y = 0 + MAX_AREA_HEIGHT/2;
        
        plane.setLocalTranslation(temp);
    }
    
    public RecDivMazeGen(AssetManager newAssetManager, int areaWidth, int areaHeight, int newMinRoomWidth, int newMinRoomHeight,
            int doorSize, int wallThickness)
    {
        assetManager = newAssetManager;
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        MAX_AREA_WIDTH = areaWidth;
        MAX_AREA_HEIGHT = areaHeight;
        WALL_THICKNESS = wallThickness;
        minRoomWidth = newMinRoomWidth;
        minRoomHeight = newMinRoomHeight;
        plane = new Geometry("Floor", new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        givePlaneSaneCoordinates();
        createBorderWalls();
        generatedMaze = new Node("Maze");
        generatedMaze.attachChild(plane);
    }
}
