package mygame;

import mygame.MapGeneration.RecDivMazeGrid;
import mygame.MapGeneration.SprinkleObjects;
import mygame.GameObjects.Gem;
import mygame.GameObjects.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;

    private BulletAppState bulletAppState;

    private boolean freeCam = false;
    private Player player;
    private List<Gem> gems;

    private void initKeys() {
        //inputManager.addMapping("Wall",  new KeyTrigger(KeyInput.KEY_SPACE));
        //inputManager.addListener(actionListener,"Wall");

        //Restart maze and change camera
        inputManager.addMapping("Restart", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Camera", new KeyTrigger(KeyInput.KEY_C));
        inputManager.addListener(actionListener, "Restart", "Camera");

        //Player controls
        inputManager.addMapping("Strafe Left",
                new KeyTrigger(KeyInput.KEY_Q),
                new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("Strafe Right",
                new KeyTrigger(KeyInput.KEY_E),
                new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Shoot",
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Strafe Left", "Strafe Right");
        inputManager.addListener(actionListener, "Rotate Left", "Rotate Right");
        inputManager.addListener(actionListener, "Walk Forward", "Walk Backward");
        inputManager.addListener(actionListener, "Jump", "Shoot");

    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {

            //Restart maze (temp)
            if (name.equals("Restart") && !keyPressed) {
                bulletAppState.getPhysicsSpace().removeAll(rootNode);
                rootNode.detachAllChildren();
                System.out.println("Restart");
                initGame();
            }

            //Switch camera
            if (name.equals("Camera") && !keyPressed) {
                if (!freeCam) {
                    //Detach chase camera
                    player.detachCamera();
                    player.stop();
                    //Set location of flycam
                    Vector3f camLocation = new Vector3f(0, 6, 6);
                    cam.setLocation(camLocation.add(player.getLocation()));
                    cam.lookAt(player.getLocation(), new Vector3f(0, 0, 0));
                    //Enable flycam
                    flyCam.setEnabled(true);
                    freeCam = true;
                } else {
                    //Disable flycam
                    flyCam.setEnabled(false);
                    //Attach chase camera
                    player.attachCamera();
                    freeCam = false;
                }

            }

            //Player controls
            player.controls(name, keyPressed);
        }
    };

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        //Testlight
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
        rootNode.addLight(dl);

        //Activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //initKeys is only here for testing individual systems.
        initKeys();
        initGame();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!freeCam) {
            player.move(tpf);
        }
        
        /*
        for(Gem gem: gems)
        {
            gem.update(tpf);
        }*/
    }

    private void initGame() {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, bulletAppState, 20, 20, 1f, 1f, 0.5f, 1, 4, 4);
        Node sceneNode = new Node("scene");
        sceneNode.attachChild(maze.generateMaze());
        
//Constructor SprinkleObjects(AssetManager newAssetManager, int treasurePointValue, int maxPointsInArea, int minDistanceToPlayer, 
//            int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
//Note: Chances are in a range of 1-100
        sprinkler = new SprinkleObjects(assetManager, 50, 1000, 10, 5, 65, 75, 40);
        Node sp = sprinkler.sprinkle();

        sceneNode.attachChild(sp);
        rootNode.attachChild(sceneNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));

        //System.out.println(sp.getChildren());
        //placeObjects(sp);

        //More confortable flycam and disable
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setEnabled(false);

        //Create player 
        //Vector3f playerLocation = sp.getChild("Player").getWorldTranslation();
        //player = new Player(assetManager,rootNode,cam,playerLocation.add(new Vector3f(0,4,0)));
        player = new Player(assetManager, rootNode, cam, new Vector3f(0,4,0));
    }

    /*private void placeObjects(Node objects) {
        gems = new ArrayList();

        for (Spatial child : objects.getChildren()) {
            Vector3f location = child.getWorldTranslation();
            switch (child.getName()) {
                case "Treasure":
                    Gem gem = new Gem(assetManager,location);
                    gems.add(gem);
                    rootNode.attachChild(gem.getGeom());
                    objects.detachChild(child);
                    break;

                case "Flower Pot":

                    break;

                case "Computer Desk":

                    break;

                case "Enemy":

                    break;

                case "Player":
                    player = new Player(assetManager, rootNode, cam, location.add(new Vector3f(0, 4, 0)));
                    break;
            }
        }
    }*/
}
