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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    List<int[]> minMaxWideList;
    List<int[]> minMaxTallList;
    List<int[]> waitingRoom;
    final float Z_HEIGHT_OF_ALL = 1f;
    final float CELL_WIDTH;
    final float CELL_HEIGHT;
    final float WALL_THICKNESS;
    final int DOOR_SIZE;
    final int MIN_CELLS_WIDE;
    final int MIN_CELLS_TALL;
    boolean cutIsHorizontal;
    
    //Temp method to test wall placement.
    public Node getNode()
    {
        return generatedMaze;
    }
    
    private int generateRandomNum(int lowerBound, int higherBound)
    {
        return ThreadLocalRandom.current().nextInt(lowerBound, higherBound);
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
        //TODO: Make door, you've only carved the place
        int doorCellNum = generateRandomNum(0, numOfCells);
        float doorSize = calcSize(DOOR_SIZE);
        float leftDownWallSize = calcSize(doorCellNum);
        float rightUpWallSize = calcSize(numOfCells - (doorCellNum + DOOR_SIZE));
        
        float[] ret = {doorSize, leftDownWallSize, rightUpWallSize};
        return ret;
    }
    
    //TODO: Clean this function into smaller pieces
    private void createWall(int gridStartCoorX, int gridStartCoorY, int numOfCells, String geomName)
    {
        Geometry[] geoms = new Geometry[2];
        Box[] boxes = new Box[2];
        
        float fullWallStartXPos = calcSize(gridStartCoorX);
        float fullWallStartYPos = calcSize(gridStartCoorY);

        float[] carveValues = carveDoor(numOfCells);
        float doorSize = carveValues[0];
        
        float leftDownWallSize = carveValues[1];
        float leftDownXPos;
        float leftDownYPos;
        
        float rightUpWallSize = carveValues[2];
        float rightUpXPos;
        float rightUpYPos;
        
        if(cutIsHorizontal)
        {
            boxes[0] = new Box(leftDownWallSize/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL/2f);
            leftDownXPos = leftDownWallSize/2f - WALL_THICKNESS;
            leftDownYPos = fullWallStartYPos + CELL_HEIGHT/2f + WALL_THICKNESS * 2.5f;
            geoms[0] = new Geometry("left" + geomName, boxes[0]);
            
            boxes[1] = new Box(rightUpWallSize/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL/2f);
            rightUpXPos = leftDownWallSize + doorSize - WALL_THICKNESS * 2f + rightUpWallSize/2f;
            rightUpYPos = fullWallStartYPos + CELL_HEIGHT/2f + WALL_THICKNESS * 2.5f;
            geoms[1] = new Geometry("right" + geomName, boxes[1]);
        }
        else
        {
            boxes[0] = new Box(WALL_THICKNESS/2f, leftDownWallSize/2f, Z_HEIGHT_OF_ALL/2f);
            leftDownXPos = fullWallStartXPos + CELL_WIDTH/2f + WALL_THICKNESS * 2.5f;
            leftDownYPos = leftDownWallSize/2f - WALL_THICKNESS;
            geoms[0] = new Geometry("down" + geomName, boxes[0]);
            
            boxes[1] = new Box(WALL_THICKNESS/2f, rightUpWallSize/2f, Z_HEIGHT_OF_ALL/2f);
            rightUpXPos = fullWallStartXPos + CELL_WIDTH/2f+ WALL_THICKNESS * 2.5f;
            rightUpYPos = leftDownWallSize + doorSize - WALL_THICKNESS * 2f + rightUpWallSize/2f;
            geoms[1] = new Geometry("up" + geomName, boxes[1]);
        }
        
        geoms[0].setMaterial(wallMat);
        geoms[1].setMaterial(wallMat);
        
        geoms[0].setLocalTranslation(leftDownXPos, leftDownYPos, 0);
        geoms[1].setLocalTranslation(rightUpXPos, rightUpYPos, 0);
        
        generatedMaze.attachChild(geoms[0]);
        generatedMaze.attachChild(geoms[1]);
    }
    
    //TODO: Disallow random numbers to craete rooms with width or height below minimums
    private void divideArea()
    {
        int randomCoor;
        int[] minMaxWide = minMaxWideList.remove(0);
        int[] minMaxTall = minMaxTallList.remove(0);
        int cellsWide = minMaxWide[1] - minMaxWide[0];
        int cellsTall = minMaxTall[1] - minMaxTall[0];
        cutIsHorizontal = isCutHorizontal(cellsWide, cellsTall);

        if(cellsWide <= MIN_CELLS_TALL || cellsTall <= MIN_CELLS_TALL)
        {
            return;
        }

        if(cutIsHorizontal)
        {
            randomCoor = generateRandomNum(minMaxTall[0] + 1, minMaxTall[1] - 1);
            createWall(minMaxWide[0], randomCoor, cellsWide, (minMaxWide[0] + ", " + randomCoor));

            minMaxWideList.add(minMaxWide);
            minMaxTallList.add(new int[] {minMaxTall[0], randomCoor});

            minMaxWideList.add(minMaxWide);
            minMaxTallList.add(new int[] {randomCoor, minMaxTall[1]});
        }
        else
        {
            randomCoor = generateRandomNum(minMaxWide[0] + 1, minMaxWide[1] - 1);
            createWall(randomCoor, minMaxTall[0], cellsTall, (randomCoor + ", " + minMaxTall[0]));

            minMaxWideList.add(new int[] {minMaxWide[0], randomCoor});
            minMaxTallList.add(minMaxTall);

            minMaxWideList.add(new int[] {randomCoor, minMaxWide[1]});
            minMaxTallList.add(minMaxTall);
        }
    }
    
    private void recursiveDivision()
    {
        while(minMaxWideList.size() > 0)
        {
            divideArea();
        }
    }
    
    private void recursiveDivision(int uselessParametre)
    {
        //Pick random spot for cut
        //Make wall
        //Generate random door area
        //Carve door
        //Repeat on resulting areas.
        int randomCoor = generateRandomNum(0, grid.length);
        cutIsHorizontal = isCutHorizontal(grid.length, grid[randomCoor].length);
        
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
        minMaxWideList.add(new int[] {0, grid.length});
        minMaxTallList.add(new int[] {0, grid[0].length});
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
            float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
    {
        generatedMaze = new Node();
        minMaxWideList = new ArrayList<>();
        minMaxTallList = new ArrayList<>();
        waitingRoom = new ArrayList<>();
        assetManager = newAssetManager;
        grid = new Cell[numCellsWide][numCellsTall];
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        WALL_THICKNESS = wallThickness;
        DOOR_SIZE = doorCellSize;
        MIN_CELLS_WIDE = minCellsWide;
        MIN_CELLS_TALL = minCellsTall;
        createMaterials();
        //createBorders(numCellsWide, numCellsTall);
        createBaseMap();
    }
}
