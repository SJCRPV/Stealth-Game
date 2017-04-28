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
    Cell[][] grid;
    final int Z_HEIGHT_OF_ALL = 1;
    final float CELL_WIDTH;
    final float CELL_HEIGHT;
    final float WALL_THICKNESS;
    final float DOOR_SIZE;
    boolean cutIsHorizontal;
    
    private void recursiveDivision()
    {
        
    }
    
    public Node generateMaze()
    {
        recursiveDivision();
        return generatedMaze;
    }
    
    private void createBorders(int numOfCellsWide, int numOfCellsTall)
    {
        float maxWidth = (WALL_THICKNESS * (numOfCellsWide + 1) + (CELL_WIDTH * numOfCellsWide));
        float maxHeight = (WALL_THICKNESS * (numOfCellsTall + 1) + (CELL_WIDTH * numOfCellsTall));
        
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
        upGeom.setLocalTranslation(maxWidth/2f, maxHeight - WALL_THICKNESS/2f, 0);
        leftGeom.setLocalTranslation(WALL_THICKNESS/2f, maxHeight/2f, 0);
        downGeom.setLocalTranslation(maxWidth/2f, WALL_THICKNESS/2f, 0);
        rightGeom.setLocalTranslation(maxWidth- WALL_THICKNESS/2f, maxHeight/2f, 0);
        
        generatedMaze.attachChild(upGeom);
        generatedMaze.attachChild(leftGeom);
        generatedMaze.attachChild(downGeom);
        generatedMaze.attachChild(rightGeom);
    }
    
    private float calcSize(int numOfCells)
    {
        return (WALL_THICKNESS * (numOfCells + 1) + (CELL_WIDTH * numOfCells));
    }
    
    private void createWall(int numOfCellsWide, int numOfCellsTall, String geomName, boolean isVertical, Vector3f topLeft)
    {
        float wallWidth = calcSize(numOfCellsWide);
        float wallHeight = calcSize(numOfCellsTall);
        
        Box box;
        if(isVertical)
        {
            box = new Box(WALL_THICKNESS/2f, wallHeight/2f, Z_HEIGHT_OF_ALL);
        }
        else
        {
            box = new Box(wallWidth/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL);
        }
        
        Geometry geom = new Geometry(geomName, box);
        geom.setMaterial(wallMat);
        
        if(isVertical)
        {
            geom.setLocalTranslation();
        }
        
        
        generatedMaze.attachChild(geom);
    }
    
    private void representOnMaze()
    {
        float maxWidth = (WALL_THICKNESS * (numOfCellsWide + 1) + (CELL_WIDTH * numOfCellsWide));
        float maxHeight = (WALL_THICKNESS * (numOfCellsTall + 1) + (CELL_WIDTH * numOfCellsTall));
    }
    
    private void createCell(int i, int j, Vector3f WCTopLeft)
    {
        //Constructor Cell(Vector3f WCTopLeft, int cellX, int cellY)
        grid[i][j] = new Cell(WCTopLeft, i, j);
        representOnMaze();
    }
    
    private void createBaseMap()
    {
        for(int i = 0; i < grid.length; i++)
        {
            float vectorDotY = (WALL_THICKNESS * (i + 1)) + (CELL_HEIGHT * i);
            for(int j = 0; j < grid[i].length; j++)
            {
                float vectorDotX = (WALL_THICKNESS * (j + 1)) + (CELL_WIDTH * j);
                Vector3f temp = new Vector3f(vectorDotX, vectorDotY, Z_HEIGHT_OF_ALL);
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
    }
    
    public RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight,
            float wallThickness, int doorCellSize)
    {
        grid = new Cell[numCellsWide][numCellsTall];
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        WALL_THICKNESS = wallThickness;
        DOOR_SIZE = doorCellSize;
        createMaterials();
        createBorders(numCellsWide, numCellsTall);
        createBaseMap();
    }
}
