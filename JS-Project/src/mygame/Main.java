package mygame;

import mygame.MapGeneration.RecDivMazeGrid;
import mygame.MapGeneration.SprinkleObjects;
import mygame.GameObjects.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Environment;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.font.Rectangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import mygame.GameObjects.Enemy;
import mygame.GameObjects.FullLight;
import mygame.GameObjects.GameObject;
import mygame.GameObjects.Gem;
import mygame.GameObjects.Torch;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;
    Player player;

    Node aNode;
    Node sceneNode;
    Node mazeNode;
    Node sprinkleNode;
    Node allEncompassingNode;
    //Node lightNode;

    private boolean freeCam = false;
    private boolean playing = true;
    private List<GameObject> gObjectsList;
    private List<FullLight> lightList;
    private List<AudioNode> audioList;
    
    BulletAppState bulletAppState;
    Material sparkleMat;
    ParticleEmitter sparkles;
    BitmapText hudText;
    
    AudioNode aAmbient;

    private void addLightToRelevantAreas(FullLight lamp_light) {
        rootNode.addLight(lamp_light.getLight());
        lightList.add(lamp_light);
    }

    private void addToWorld() {
        for (GameObject gObject : gObjectsList) {
            if (gObject.getClassName().equals("Torch")) {
                FullLight light = new FullLight(assetManager, viewPort, gObject, ColorRGBA.Orange.mult(ColorRGBA.Yellow), 8f, new Vector3f(0, 0.55f, 0));
                addLightToRelevantAreas(light);
                
                Torch torch = (Torch)gObject;
                torch.loadAudio();
                aNode.attachChild(torch.getAudioNode());
                audioList.add(torch.getAudioNode());
            }

            if (gObject.getClassName().equals("Crate")) {
                RigidBodyControl cratePhy = new RigidBodyControl(0f);
                gObject.getGeom().addControl(cratePhy);
                bulletAppState.getPhysicsSpace().add(cratePhy);
            }

            if (gObject.getClassName().equals("Objective")) {
                FullLight light = new FullLight(assetManager, viewPort, gObject, ColorRGBA.Yellow.mult(0.8f), 10f, Vector3f.UNIT_Y);
                addLightToRelevantAreas(light);
            }
        }
    }

    private void setFlyCamSettings() {
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setEnabled(false);
    }

    private void lightScene() {
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.4f));
        dl.setDirection(new Vector3f(-1f, -1f, -1));
        allEncompassingNode.addLight(dl);
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White.mult(0.4f));
        dl2.setDirection(new Vector3f(1f, 1f, 1));
        allEncompassingNode.addLight(dl2);
    }
    
    private void preparePlayer()
    {
        player = sprinkler.getPlayer();
        player.getNode().setShadowMode(ShadowMode.Cast);
        allEncompassingNode.attachChild(player.getNode());
        sprinkleNode.detachChild(player.getNode());
    }

    private Node prepareSprinkleNode() {
//Constructor SprinkleObjects(AssetManager newAssetManager, Camera cam, Node mazeNode, int treasurePointValue,
//            int minDistanceToPlayer, int maxEnemiesPerRoom, int maxTorchesPerRoom, int maxGemsPerRoom, 
//            float enemyChance, float objectChance, float treasureChance)
        sprinkler = new SprinkleObjects(assetManager, cam, mazeNode, Gem.getGemValue(), 3, 2, 1, 2, 30, 80, 70);
        Node node = new Node();
        node.attachChild(sprinkler.sprinkle());
        node.setShadowMode(ShadowMode.Receive);

        return node;
    }

    private Node prepareMazeNode() {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, bulletAppState, 15, 15, 1f, 1f, 0.5f, 1, 4, 4);
        Node node = new Node();
        node.attachChild(maze.generateMaze());
        node.setShadowMode(ShadowMode.Receive);

        return node;
    }

    private void createHUD() {
        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 2);      // font size
        hudText.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
        hudText.setAlignment(BitmapFont.Align.Center);
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setLocalTranslation(0, settings.getHeight(), 0); // position
        hudText.setText("Score: " + Integer.toString(player.getScore()));             // the text
        guiNode.attachChild(hudText);
    }
    
    private void initAudio() {
       
        audioRenderer.setEnvironment(new Environment(Environment.Dungeon));
        
        aNode.attachChild(Gem.getAudioNode());
        audioList.add(Gem.getAudioNode());

        aNode.attachChild(Player.getAudioNode());
        audioList.add(Player.getAudioNode());
        
        aAmbient = new AudioNode(assetManager, "Sounds/win.ogg", true);
        //aAmbient.setLooping(true);  // activate continuous playing
        aAmbient.setPositional(false);
        aAmbient.setVolume(0.2f);
        aNode.attachChild(aAmbient);
    }

    private void initGame() {
        
        allEncompassingNode = new Node("newRoot");
        
        sceneNode = new Node("Scene");
        mazeNode = prepareMazeNode();
        
        sprinkleNode = prepareSprinkleNode();
        gObjectsList = sprinkler.getGOList();
        preparePlayer();

        sceneNode.attachChild(mazeNode);
        sceneNode.attachChild(sprinkleNode);
        //sceneNode.attachChild(lightNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));
        
        lightList = new ArrayList();
        lightScene();
        
        allEncompassingNode.attachChild(sceneNode);
        allEncompassingNode.setShadowMode(ShadowMode.Receive);

        //More confortable flycam and disable
        setFlyCamSettings();

        //Init hud text
        createHUD();
        
        playing = true;
        aNode = new Node("Audio");
        audioList = new ArrayList();
        initAudio();
        
        addToWorld();
        rootNode.attachChild(aNode);
        rootNode.attachChild(allEncompassingNode);
    }

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

    private void switchCamera() {
        if (!freeCam) {
            //Detach chase camera
            player.detachCamera();
            if (playing) {
                player.stop();
            }
            //Set location of flycam
            Vector3f camLocation = new Vector3f(0, 2, 2);
            cam.setLocation(camLocation.add(player.getWorldTranslation()));
            cam.lookAt(player.getWorldTranslation(), new Vector3f(0, 0, 0));
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

    private void restartGame() {

        for (AudioNode sound : audioList) {
            sound.stop();
        }
        
        aAmbient.stop();
        
        for (GameObject gObject : gObjectsList) {
            if (gObject.getClassName().equals("Enemy")) {
                Enemy e = (Enemy) gObject;
                e.stopWalk();              
            }
        }
        bulletAppState.getPhysicsSpace().removeAll(rootNode);
        rootNode.detachAllChildren();
        guiNode.detachAllChildren();
        System.out.println("Restart");
        initGame();
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            //Restart maze (temp)
            if (name.equals("Restart") && !keyPressed && !freeCam) {
                restartGame();
            }

            if (name.equals("Camera") && !keyPressed) {
                switchCamera();
            }

            //Player controls
            if (playing && !freeCam) {
                player.controls(name, keyPressed);
            }
        }
    };

    @Override
    public void simpleInitApp() {
        //To avoid not showing objects behind player.
        cam.setFrustumPerspective(45, settings.getWidth() / settings.getHeight(), 0.1f, 1000f);

        //Activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        initKeys();
        initGame();
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    private void handleCollisions(GameObject gObject) {

        if (gObject.handleCollisions(player)) {
            if (gObject.getClassName().equals("Objective")) {
                if (playing) {
                    playing = false;
                    player.dance();
                    aAmbient.play();

                    hudText.setLocalTranslation(0, settings.getHeight() / 2, 0); // position
                    hudText.setText("You found the treasure! \n Press R to play again \n Your score: " + Integer.toString(player.getScore()));             // the text
                }
            }

            if (gObject.getClassName().equals("Enemy")) {
                if (playing) {

                    player.playAudioInstance("death");
                    player.playAudioInstance("hit");

                    Enemy e = (Enemy) gObject;
                    e.stop();
                    playing = false;
                    player.die();

                    hudText.setLocalTranslation(0, settings.getHeight() / 2, 0); // position
                    hudText.setText("You are dead :( \n Press R to play again");             // the text
                }
            }

            if (gObject.getClassName().equals("Gem") && playing) {

                Gem gem = (Gem) gObject;
                gem.playAudioInstance();
                
                ParticleEmitter temp = gem.prepareParticleExplosion(gObject);
                rootNode.attachChild(temp);
                temp.emitAllParticles();
                hudText.setText("Score: " + Integer.toString(player.getScore()));
                gObject.getNode().removeFromParent();
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (!freeCam) {
            player.move(tpf);
        }
            listener.setLocation(cam.getLocation());
            listener.setRotation(cam.getRotation());
            
        for (GameObject gObject : gObjectsList) {
            gObject.update(tpf);
            handleCollisions(gObject);
            
            /**
            if (gObject.getClassName().equals("Enemy")) {
                Enemy e = (Enemy) gObject;
                e.getAudio().move(gObject.getWorldTranslation());
                System.out.println(gObject.getWorldTranslation());
            }**/
        }
        for (FullLight light : lightList) {
            light.setRadius(8 + (float) (Math.random()));
            if (!light.castRay(allEncompassingNode, player)) {
                light.removeShadow();
            } else {
                light.addShadow();
            }
        }
    }
}
