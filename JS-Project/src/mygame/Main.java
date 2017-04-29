package mygame;

import com.jme3.app.SimpleApplication;
//import com.jme3.material.Material;
//import com.jme3.math.ColorRGBA;
//import com.jme3.scene.Geometry;
//import com.jme3.scene.shape.Box;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight,
//                          float wallThickness, int doorCellSize)
        RecDivMazeGrid maze = new RecDivMazeGrid(assetManager, 15, 15, 0.125f, 0.125f, 0.005f, 1);
        //rootNode.attachChild(maze.generateMaze());
        rootNode.attachChild(maze.getNode());
    }
}
