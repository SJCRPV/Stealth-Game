package mygame;

import com.jme3.app.SimpleApplication;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    RecDivMazeGrid maze;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, 
//                      int numCellsTall, float cellWidth, float cellHeight, float wallThickness, int doorCellSize)
        maze = new RecDivMazeGrid(assetManager, 20, 20, 0.5f, 0.5f, 0.25f, 1);
        rootNode.attachChild(maze.generateMaze());
        //rootNode.attachChild(maze.getNode());
    }
}
