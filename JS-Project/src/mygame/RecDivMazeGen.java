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
    Material wallMat;
    Material floorMat;
    Integer nameCounter = 0;
    Vector3f centreOfCurrAreaInWC;
    final float zHeightOfAll = 1;
    final int MAX_AREA_WIDTH;
    final int MAX_AREA_HEIGHT;
    final float WALL_THICKNESS;
    float minRoomWidth;
    float minRoomHeight;
    float doorSize;
    boolean cutIsHorizontal;
    
    //Temp method. Delete when walls are being placed correctly
    public Node getNode()
    {
        return generatedMaze;
    }
    
    private boolean isCutHorizontal(float quadWidth, float quadHeight)
    {
        return quadHeight > quadWidth;
    }
    
    private Box[] carveOpening(Box box)
    {
        //TODO: Randomize the placement of the doors. You can probably do it with one side being "p" and the other side being "1-p"
        Box[] boxesToReturn = new Box[2];
        
        if(cutIsHorizontal)
        {
            boxesToReturn[0] = new Box(box.xExtent/2f - doorSize/2f, box.yExtent, zHeightOfAll);
        }
        else
        {
            boxesToReturn[0] = new Box(box.xExtent, box.yExtent/2f - doorSize/2f, zHeightOfAll);
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
        String sLeftOrUp = "BisectionLeftOrUp" + nameCounter.toString();
        String sRightOrDown = "BisectionRightOrDown" + nameCounter.toString();
        nameCounter++;
        
        if(cutIsHorizontal)
        {
            bisection = new Box(area.getWidth(), WALL_THICKNESS/2f, zHeightOfAll);
        }
        else
        {
            bisection = new Box(WALL_THICKNESS/2f, area.getHeight(), zHeightOfAll);
        }
        
        Box[] carvedBoxes = carveOpening(bisection);
        
        if(cutIsHorizontal)
        {
            offsetAfterCarve = new Vector3f(centreOfBisection.x - carvedBoxes[0].xExtent, 0, 0);
        }
        else
        {
            offsetAfterCarve = new Vector3f(0, centreOfBisection.y - carvedBoxes[0].yExtent, 0);
        }
        
        bisectionGeoms[0] = new Geometry(sLeftOrUp, carvedBoxes[0]);
        bisectionGeoms[0].setMaterial(wallMat);
        bisectionGeoms[0].setLocalTranslation(centreOfBisection.subtract(offsetAfterCarve));
        
        bisectionGeoms[1] = new Geometry(sRightOrDown, carvedBoxes[1]);
        bisectionGeoms[1].setLocalTranslation(centreOfBisection.add(offsetAfterCarve));
        bisectionGeoms[1].setMaterial(wallMat);
        
        return bisectionGeoms;
    }
    
    private void recursiveDivision(Quad area)
    {
        if(area.getWidth() > minRoomWidth && area.getHeight() > minRoomHeight)
        {
            //Bisect,
            //Fill,
            //Carve,
            //TODO: Make a door. Box of size doorSize, Green material. Insert Door,
            //Recursion.
            Geometry[] bisectionGeoms = bisectArea(area);
            generatedMaze.attachChild(bisectionGeoms[0]);
            generatedMaze.attachChild(bisectionGeoms[1]);
            
            //Error, taking into account the box instead of the actual area. Also take into account WALL_THICKNESS/2f
            Box tempBox = (Box)bisectionGeoms[0].getMesh();
            Quad newArea;
            if(cutIsHorizontal)
            {
                newArea = new Quad(tempBox.xExtent, area.getHeight()/2f);
            }
            else
            {
                newArea = new Quad(area.getWidth()/2f, tempBox.yExtent);
            }
            recursiveDivision(newArea);
            
            tempBox = (Box)bisectionGeoms[1].getMesh();
            if(cutIsHorizontal)
            {
                newArea = new Quad(tempBox.xExtent, area.getHeight()/2f);
            }
            else
            {
                newArea = new Quad(area.getWidth()/2f, tempBox.yExtent);
            }
            recursiveDivision(newArea);
        }
        return;
    }
    
    public Node generateMaze()
    {
        recursiveDivision(new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        return generatedMaze;
    }
    
    private void createBorderWalls()
    {
        Box up = new Box(MAX_AREA_WIDTH/2f, WALL_THICKNESS, zHeightOfAll);
        Box left = new Box(WALL_THICKNESS, MAX_AREA_HEIGHT/2f, zHeightOfAll);
        Box down = new Box(MAX_AREA_WIDTH/2f, WALL_THICKNESS, zHeightOfAll);
        Box right = new Box(WALL_THICKNESS, MAX_AREA_HEIGHT/2f, zHeightOfAll);
        
        Geometry upGeom = new Geometry("UpBorder", up);
        Geometry leftGeom = new Geometry("LeftBorder", left);
        Geometry downGeom = new Geometry("DownBorder", down);
        Geometry rightGeom = new Geometry("RightBorder", right);
        
        upGeom.setMaterial(wallMat);
        leftGeom.setMaterial(wallMat);
        downGeom.setMaterial(wallMat);
        rightGeom.setMaterial(wallMat);
        
        upGeom.setLocalTranslation(MAX_AREA_WIDTH/2f, MAX_AREA_HEIGHT - WALL_THICKNESS/2f, 0);
        leftGeom.setLocalTranslation(WALL_THICKNESS/2f, MAX_AREA_HEIGHT/2f, 0);
        downGeom.setLocalTranslation(MAX_AREA_WIDTH/2f, WALL_THICKNESS/2f, 0);
        rightGeom.setLocalTranslation(MAX_AREA_WIDTH - WALL_THICKNESS/2f, MAX_AREA_HEIGHT/2f, 0);
        
        generatedMaze.attachChild(upGeom);
        generatedMaze.attachChild(leftGeom);
        generatedMaze.attachChild(downGeom);
        generatedMaze.attachChild(rightGeom);
    }
    
    public RecDivMazeGen(AssetManager newAssetManager, int areaWidth, int areaHeight, float newMinRoomWidth, float newMinRoomHeight,
            float newDoorSize, float wallThickness)
    {
        assetManager = newAssetManager;
        generatedMaze = new Node("Maze");
        MAX_AREA_WIDTH = areaWidth;
        MAX_AREA_HEIGHT = areaHeight;
        WALL_THICKNESS = wallThickness;
        minRoomWidth = newMinRoomWidth;
        minRoomHeight = newMinRoomHeight;
        doorSize = newDoorSize;
        wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMat.setColor("Color", ColorRGBA.Blue);
        floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Red);
        plane = new Geometry("Floor", new Quad(MAX_AREA_WIDTH, MAX_AREA_HEIGHT));
        plane.setMaterial(floorMat);
        wallMat.setColor("Color", ColorRGBA.Blue);
        createBorderWalls();
        generatedMaze.attachChild(plane);
    }
}
