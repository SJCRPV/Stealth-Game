/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
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
    final float DOOR_SIZE;
    boolean cutIsHorizontal;
    
    //Temp method to test wall placement.
    public Node getNode()
    {
        return generatedMaze;
    }
    
    private float calcSize(int numOfCells)
    {
        return ((WALL_THICKNESS + CELL_WIDTH * 2) * numOfCells) + WALL_THICKNESS;
    }
    
    private Vector3f centreCoords(Vector3f vector)
    {
        return new Vector3f();
    }
    
    private void recursiveDivision()
    {
        //Generate random cut area
        //Make wall
        //Generate random door area
        //Carve door
        //Repeat on resulting areas.
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
    
    private void createWall(int numOfCellsWide, int numOfCellsTall, String geomName, boolean isVertical, Vector3f centre)
    {
        float wallWidth = calcSize(numOfCellsWide);
        float wallHeight = calcSize(numOfCellsTall);
        
        Box box;
        if(isVertical)
        {
            box = new Box(WALL_THICKNESS/2f, CELL_HEIGHT/2f, Z_HEIGHT_OF_ALL/2f);
        }
        else
        {
            box = new Box(CELL_WIDTH/2f, WALL_THICKNESS/2f, Z_HEIGHT_OF_ALL/2f);
        }
        
        Geometry geom = new Geometry(geomName, box);
        geom.setMaterial(wallMat);
        
        geom.setLocalTranslation(wallWidth/2f, wallHeight/2f, 0);
        
        generatedMaze.attachChild(geom);
    }
    
    private void representOnMaze(Vector3f translation)
    {
        Box box = new Box(CELL_WIDTH, CELL_HEIGHT, 0f);
        Geometry geom = new Geometry("Cell", box);
        geom.setMaterial(cellMat);
        translation.x += CELL_WIDTH/2f;
        translation.y += CELL_HEIGHT/2f;
        geom.setLocalTranslation(translation);
        
        generatedMaze.attachChild(geom);
    }
    
    private void createCell(int i, int j, Vector3f translation)
    {
        //Constructor Cell(Vector3f WCTopLeft, int cellX, int cellY)
        grid[i][j] = new Cell(translation, i, j);
        representOnMaze(translation);
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
