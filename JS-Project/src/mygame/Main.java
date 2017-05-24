package mygame;

import mygame.MapGeneration.RecDivMazeGrid;
import mygame.MapGeneration.SprinkleObjects;
import mygame.GameObjects.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;
import mygame.GameObjects.GameObject;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    
    protected static int GEMVALUE = 50;
    protected static int MAXSCORE = 1000;
    
    
    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;
    Player player;
    int score;
    
    
    Node sprinkleNode;

    private BulletAppState bulletAppState;

    private boolean freeCam = false;
    private List<GameObject> gObjectsList;

    private void initKeys() {
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
            if (name.equals("Restart") && !keyPressed && !freeCam) {
                restartGame();
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

        //To avoid not showing objects behind player. Does not work well with flycam
        cam.setFrustumPerspective(45, settings.getWidth() / settings.getHeight(), 0.0001f, 1000f);

        //Second test light
        /**
         * SpotLight spot = new SpotLight(); spot.setSpotRange(100f); //
         * distance spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner
         * light cone (central beam) spot.setSpotOuterAngle(35f *
         * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
         * spot.setColor(ColorRGBA.Red.mult(1.3f)); // light color
         * spot.setPosition(cam.getLocation()); // shine from camera loc
         * spot.setDirection(cam.getDirection()); // shine forward from camera
         * loc rootNode.addLight(spot);*
         */
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

        for (GameObject gObject : gObjectsList) {
            gObject.update(tpf);
            
            //Handle collisions
            if (gObject.getCName().equals("Objective")) {
                CollisionResults results = new CollisionResults();
                BoundingVolume bv = gObject.getGeom().getWorldBound();
                player.getSpatial().collideWith(bv, results);

                if (results.size() > 0) {
                    restartGame();
                }
            }
            
            if (gObject.getCName().equals("Gem")) {
                CollisionResults results = new CollisionResults();
                BoundingVolume bv = gObject.getGeom().getWorldBound();
                player.getSpatial().collideWith(bv, results);

                if (results.size() > 0) {
                    score+=GEMVALUE;
                    System.out.println(score);
                   sprinkleNode.detachChild(gObject.getSpatial());
                }
            }
        }
    }

    private Player findPlayer() {
        for (int i = 0; i < gObjectsList.size(); i++) {
            if (gObjectsList.get(i).getCName().equalsIgnoreCase("Player")) {
                return (Player) gObjectsList.get(i);
            }
        }
        return null;
    }

    private void initGame() {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, bulletAppState, 15, 15, 1f, 1f, 0.5f, 1, 4, 4);
        Node sceneNode = new Node("scene");
        sceneNode.attachChild(maze.generateMaze());
<<<<<<< HEAD
        
//Constructor public SprinkleObjects(AssetManager newAssetManager, Camera cam, Vector3f rootWC, int treasurePointValue,
//        int maxPointsInArea, int minDistanceToPlayer, int maxObjectsPerRoom, float enemyChance, float objectChance, 
//        float treasureChance)
//Note: Chances are in a range of 1-100
        sprinkler = new SprinkleObjects(assetManager, cam, rootNode.getWorldTranslation(), 50, 1000, 10, 5, 60, 65, 40);
        Node sp = sprinkler.sprinkle();
=======

//Constructor SprinkleObjects(AssetManager newAssetManager, int treasurePointValue, int maxPointsInArea, int minDistanceToPlayer, 
//        int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
//Note: Chances are in a range of 1-100
        sprinkler = new SprinkleObjects(assetManager, cam, GEMVALUE, MAXSCORE, 10, 5, 80, 90, 90);
        sprinkleNode = sprinkler.sprinkle();
>>>>>>> c6a0e20d232fcb10f267d756cb26e0b36e7b608d
        gObjectsList = sprinkler.getGOList();
        sceneNode.attachChild(sprinkleNode);

        rootNode.attachChild(sceneNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));

        player = findPlayer();
        //rootNode.attachChild(player.getCharNode());
        //More confortable flycam and disable
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setEnabled(false);

        //player = new Player(assetManager);
        player = new Player(assetManager, rootNode, cam, new Vector3f(0, 4, 0));
        score = 0;
    }

    private void restartGame() {
        bulletAppState.getPhysicsSpace().removeAll(rootNode);
        rootNode.detachAllChildren();
        System.out.println("Restart");
        initGame();
    }

}
