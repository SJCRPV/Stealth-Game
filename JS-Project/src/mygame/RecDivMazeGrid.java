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

/**
 *
 * @author SJCRPV
 */
public class RecDivMazeGrid {
    
    Node generatedMaze;
    AssetManager assetManager;
    Geometry plane;
    Material wallMat;
    Material floorMat;
    Material cellMat;
    Cell[][] grid;
    final float Z_HEIGHT_OF_ALL = 1f;
    final float CELL_WIDTH;
    final float CELL_HEIGHT;
    final float WALL_THICKNESS;
    final int DOOR_SIZE;
    boolean cutIsHorizontal;
    
    //Temp method to test wall placement.
    public Node getNode()
    {
        return generatedMaze;
    }
    
    private int generateRandomNum(int lowerBound, int higherBound)
    {
        return (int)(Math.random() * higherBound + lowerBound);
    }
    
    private boolean isCutHorizontal(int cellsWide, int cellsTall)
    {
        if(cellsTall == cellsWide)
        {   
            return generateRandomNum(1, 2) == 1;
        }
        
        return cellsTall > cellsWide;
    }
    
    private float calcSize(int numOfCells)
    {
        return ((WALL_THICKNESS + CELL_WIDTH * 2) * numOfCells);
    }
    
    private float[] carveDoor(int numOfCells)
    {
        int doorCoor = generateRandomNum(0, numOfCells);
        float leftDownWallSize = calcSize(doorCoor);
        float rightUpWallSize = calcSize(numOfCells - (doorCoor + DOOR_SIZE));
        
        float[] ret = {doorCoor, leftDownWallSize, rightUpWallSize};
        return ret;
    }
    
    private void createWall(int gridStartCoorX, int gridStartCoorY, int numOfCells, String geomName)
    {
        Geometry[] geoms = new Geometry[2];
        Box[] boxes = new Box[2];
        
        float fullWallSize = calcSize(numOfCells);
        float fullWallXPos = calcSize(gridStartCoorX);
        float fullWallYPos = calcSize(gridStartCoorY);

        float[] carveValues = carveDoor(numOfCells);
        int doorCellNum = (int)carveValues[0];
        
        float leftDownWallSize = carveValues[1];
        float leftDownXPos;
        float leftDownYPos;
        
        float rightUpWallSize = carveValues[2];
        float rightUpXPos;
        float rightUpYPos;
        
        if(cutIsHorizontal)
        {
            boxes[0] = new Box(leftDownWallSize/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL/2f);
            leftDownXPos = fullWallXPos - (leftDownWallSize);
            leftDownYPos = fullWallYPos - WALL_THICKNESS * 1.5f;
            geoms[0] = new Geometry("left" + geomName, boxes[0]);
            
            boxes[1] = new Box(rightUpWallSize/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL/2f);
            rightUpXPos = fullWallXPos + (rightUpWallSize/2f);
            rightUpYPos = fullWallYPos + WALL_THICKNESS * 1.5f;
            geoms[1] = new Geometry("right" + geomName, boxes[1]);
        }
        else
        {
            boxes[0] = new Box(WALL_THICKNESS/2f, leftDownWallSize/2f, Z_HEIGHT_OF_ALL/2f);
            leftDownXPos = fullWallXPos - WALL_THICKNESS * 1.5f;
            leftDownYPos = fullWallYPos - (leftDownWallSize);
            geoms[0] = new Geometry("down" + geomName, boxes[0]);
            
            boxes[1] = new Box(WALL_THICKNESS/2f, rightUpWallSize/2f, Z_HEIGHT_OF_ALL/2f);
            rightUpXPos = fullWallXPos + WALL_THICKNESS * 1.5f;
            rightUpYPos = fullWallYPos + (rightUpWallSize/2f);
            geoms[1] = new Geometry("up" + geomName, boxes[1]);
        }
        geoms[0].setMaterial(wallMat);
        geoms[1].setMaterial(wallMat);
        
        geoms[0].setLocalTranslation(leftDownXPos, leftDownYPos, 0);
        geoms[1].setLocalTranslation(rightUpXPos, rightUpYPos, 0);
        
        generatedMaze.attachChild(geoms[0]);
        generatedMaze.attachChild(geoms[1]);
    }
    
    //This will only be called at the start. At the end of the function call the overloaded method. That one will be recursive
    private void recursiveDivision()
    {
        //Pick random spot for cut
        //Make wall
        //Generate random door area
        //Carve door
        //Repeat on resulting areas.
        cutIsHorizontal = isCutHorizontal(grid.length, grid[0].length);
        int randomCoor = generateRandomNum(1, grid.length-1);
        
        if(cutIsHorizontal)
        {
            createWall(0, randomCoor, grid[randomCoor].length, (0 + ", " + randomCoor));
        }
        else
        {
            createWall(randomCoor, 0, grid.length, (randomCoor + ", " + 0));
        }
    }
    
    public Node generateMaze()
    {
        recursiveDivision();
        return generatedMaze;
    }
    
    private void createBorders(int numOfCellsWide, int numOfCellsTall)
    {
        float maxWidth = calcSize(numOfCellsWide);
        float maxHeight = calcSize(numOfCellsTall);
        
        Box up = new Box(maxWidth/2f, WALL_THICKNESS, Z_HEIGHT_OF_ALL);
        Box left = new Box(WALL_THICKNESS, maxHeight/2f, Z_HEIGHT_OF_ALL);
        Box down = new Box(maxWidth/2f, WALL_THICKNESS, Z_HEIGHT_OF_ALL);
        Box right = new Box(WALL_THICKNESS, maxHeight/2f, Z_HEIGHT_OF_ALL);
        
        Geometry upGeom = new Geometry("UpBorder", up);
        Geometry leftGeom = new Geometry("LeftBorder", left);
        Geometry downGeom = new Geometry("DownBorder", down);
        Geometry rightGeom = new Geometry("RightBorder", right);
        
        upGeom.setMaterial(wallMat);
        leftGeom.setMaterial(wallMat);
        downGeom.setMaterial(wallMat);
        rightGeom.setMaterial(wallMat);
        
        //Note: This will probably create clipping because all the corners will have 2 walls in them.
        upGeom.setLocalTranslation(maxWidth/2f, maxHeight, 0);
        leftGeom.setLocalTranslation(0, maxHeight/2f, 0);
        downGeom.setLocalTranslation(maxWidth/2f, 0, 0);
        rightGeom.setLocalTranslation(maxWidth, maxHeight/2f, 0);
        
        generatedMaze.attachChild(upGeom);
        generatedMaze.attachChild(leftGeom);
        generatedMaze.attachChild(downGeom);
        generatedMaze.attachChild(rightGeom);
    }
    
    private void representOnMaze(Vector3f position)
    {
        Box box = new Box(CELL_WIDTH, CELL_HEIGHT, 0f);
        Geometry geom = new Geometry("Cell", box);
        geom.setMaterial(cellMat);
        position.x += CELL_WIDTH/2f;
        position.y += CELL_HEIGHT/2f;
        geom.setLocalTranslation(position);
        
        generatedMaze.attachChild(geom);
    }
    
    private void createCell(int x, int y, Vector3f position)
    {
        //Constructor Cell(Vector3f position, int cellX, int cellY)
        grid[x][y] = new Cell(position, x, y);
        representOnMaze(position);
    }
    
    private void createBaseMap()
    {
        for(int i = 0; i < grid.length; i++)
        {
            float vectorDotY = calcSize(i);
            for(int j = 0; j < grid[i].length; j++)
            {
                float vectorDotX = calcSize(j);
                Vector3f temp = new Vector3f(vectorDotX, vectorDotY, 0);
                createCell(i, j, temp);
            }
        }
    }
    
    private void createMaterials()
    {
        wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMat.setColor("Color", ColorRGBA.Blue);
        floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Red);
        cellMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cellMat.setColor("Color", ColorRGBA.Green);
    }
    
    public RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight,
            float wallThickness, int doorCellSize)
    {
        generatedMaze = new Node();
        assetManager = newAssetManager;
        grid = new Cell[numCellsWide][numCellsTall];
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        WALL_THICKNESS = wallThickness;
        DOOR_SIZE = doorCellSize;
        createMaterials();
        //createBorders(numCellsWide, numCellsTall);
        createBaseMap();
    }
}
