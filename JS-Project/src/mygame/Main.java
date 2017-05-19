package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
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

    private RigidBodyControl floor_phy;

    private boolean freeCam = false;
    
    private Player player;

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
                rootNode.detachAllChildren();
                System.out.println("Restart");
                initGame();
            }

            //Switch camera
            if (name.equals("Camera") && !keyPressed) {
                if (!freeCam) {
                    //Detach chase camera
                    player.detachCamera();
                    //Set location of flycam
                    cam.setLocation(new Vector3f(0, 10, 0));
                    cam.lookAt(new Vector3f(0, 0, 8), new Vector3f(0, 0, 0));
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
            player.controls(name,keyPressed);

        }
    };

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        //Activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //initKeys is only here for testing individual wall placement. Just comment the line with the root node and uncomment
        //this one to activate it. Each step is done by pressing the Space key.
        initKeys();
        initGame();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!freeCam) {
            player.move();
        }
    }

    private void initGame() {
        //Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, 20, 20, 1f, 1f, 0.5f, 1, 4, 4);

//Constructor SprinkleObjects(AssetManager newAssetManager, int numOfObjectsToSprinkle, int treasurePointValue,
//        int maxPointsInArea)
        sprinkler = new SprinkleObjects(assetManager, 50, 100, 1000);

        Node sceneNode = new Node("scene");
        sceneNode.attachChild(maze.generateMaze());
        sceneNode.attachChild(sprinkler.sprinkle());
        rootNode.attachChild(sceneNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));
        
        // Create a floor (temp)
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("stone_floor.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        Box floor = new Box(30f, 0.1f, 30f);
        floor.scaleTextureCoordinates(new Vector2f(3, 6));
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, 0, 8);
        this.rootNode.attachChild(floor_geo);
        floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
        
        player = new Player(assetManager,rootNode,cam,new Vector3f(0,50,0));

        //Flycam speed and disable
        flyCam.setMoveSpeed(40);
        flyCam.setEnabled(false);
    }
}
