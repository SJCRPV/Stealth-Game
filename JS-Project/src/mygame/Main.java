package mygame;

import com.jme3.app.SimpleApplication;
//import com.jme3.material.Material;
//import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
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
//public RecDivMazeGen(AssetManager newAssetManager, int areaWidth, int areaHeight, float newMinRoomWidth, 
//        float newMinRoomHeight, float doorSize, float wallThickness)
        
        RecDivMazeGen maze = new RecDivMazeGen(assetManager, 5, 5, 0.125f, 0.125f, 0.025f, 0.00625f);
        rootNode.attachChild(maze.generateMaze());
        //rootNode.attachChild(maze.getNode());
    }
}
