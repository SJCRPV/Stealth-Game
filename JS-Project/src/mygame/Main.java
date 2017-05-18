package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import java.util.List;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;
    
    private void initKeys()
    {
        //inputManager.addMapping("Wall",  new KeyTrigger(KeyInput.KEY_SPACE));
        //inputManager.addListener(actionListener,"Wall");
        
        //Restart maze
         inputManager.addMapping("Restart",  new KeyTrigger(KeyInput.KEY_R));
         inputManager.addListener(actionListener,"Restart");
    }
    
    private ActionListener actionListener = new ActionListener()
    {
        @Override 
        public void onAction(String name, boolean keyPressed, float tpf)
        {
             if (name.equals("Restart") && !keyPressed) {
                 rootNode.detachAllChildren();
                 System.out.println("restart");
                 initGame();
            }

        }
    };
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
        //initKeys is only here for testing individual wall placement. Just comment the line with the root node and uncomment
        //this one to activate it. Each step is done by pressing the Space key.
        initKeys();
        initGame();

    }
    
    private void initGame()
    {
        //Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, 20, 20, 1f, 1f, 0.5f, 1, 4, 4);
        
//Constructor SprinkleObjects(AssetManager newAssetManager, int numOfObjectsToSprinkle, int treasurePointValue,
//        int maxPointsInArea)
        sprinkler = new SprinkleObjects(assetManager, 50, 100, 1000);
        
        rootNode.attachChild(maze.generateMaze());
        rootNode.attachChild(sprinkler.sprinkle());
        //rootNode.rotateUpTo(new Vector3f(0,0,-1));
    }
}
