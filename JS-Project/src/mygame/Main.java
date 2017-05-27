package mygame;

import mygame.MapGeneration.RecDivMazeGrid;
import mygame.MapGeneration.SprinkleObjects;
import mygame.GameObjects.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;
import mygame.GameObjects.GameObject;
import mygame.GameObjects.Gem;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author SJCRPV
 */
public class Main extends SimpleApplication {

    private static final int GEMVALUE = 50;
    private static final int MAXSCORE = 1000;
    private static final int SHADOWMAP_SIZE = 1024;

    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;
    Player player;
    int score;

    Node sceneNode;
    Node sprinkleNode;
    Node allEncompassingNode;

    private BulletAppState bulletAppState;

    private boolean freeCam = false;
    private List<GameObject> gObjectsList;
    private List<PointLight> lightList;

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

        //To avoid not showing objects behind player. Does not work well with flycam
        cam.setFrustumPerspective(45, settings.getWidth() / settings.getHeight(), 0.0001f, 1000f);

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

        for (GameObject gObject : gObjectsList) 
        {
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
                    sprinkleNode.detachChild(gObject.getNode());
                    score += GEMVALUE;
                    System.out.println(score);
                }
            }
        }
        
         for (PointLight light : lightList) {
             light.setRadius(8 + (float) (Math.random()));
         }
    }

    private Player findPlayer()
    {
        for(int i = 0; i < gObjectsList.size(); i++)
        {
            if(gObjectsList.get(i).getCName().equalsIgnoreCase("Player"))
            {
                return (Player)gObjectsList.get(i);
            }
        }
        return null;
    }
    
    private void renderShadows(Node relevantNode, Light light, ShadowMode shadowMode)
    {
        //WARNING: Se calhar não é preciso fazer shadows das luzes direcionais que iluminam o mapa em geral. Se calhar, apenas
        //as luzes criadas pelas tochas e afins chegam.
        relevantNode.setShadowMode(shadowMode);
        
        
        if(light.getClass().equals(PointLight.class))
        {
            PointLightShadowRenderer pointShadowRend = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
            pointShadowRend.setLight((PointLight)light);
            viewPort.addProcessor(pointShadowRend);
            
            PointLightShadowFilter pointShadowFilter = new PointLightShadowFilter(assetManager, SHADOWMAP_SIZE);
            pointShadowFilter.setLight((PointLight)light);
            pointShadowFilter.setEnabled(true);
            FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
            filterPostProcessor.addFilter(pointShadowFilter);
            viewPort.addProcessor(filterPostProcessor);
        }
        else if(light.getClass().equals(DirectionalLight.class))
        {
            DirectionalLightShadowRenderer dirShadowRend = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
            dirShadowRend.setLight((DirectionalLight)light);
            viewPort.addProcessor(dirShadowRend);
            
            DirectionalLightShadowFilter dirShadowFilter = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
            dirShadowFilter.setLight((DirectionalLight)light);
            dirShadowFilter.setEnabled(true);
            FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);
            filterPostProcessor.addFilter(dirShadowFilter);
            viewPort.addProcessor(filterPostProcessor);
        }
    }
    
    private void initGame() {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, bulletAppState, 15, 15, 1f, 1f, 0.5f, 1, 4, 4);
        sceneNode = new Node("scene");
        sceneNode.attachChild(maze.generateMaze());
        //sceneNode.setShadowMode(ShadowMode.Cast);
        
        allEncompassingNode = new Node("lights");
        
        lightList = new ArrayList();
                
        //Light the scene
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.4f));
        dl.setDirection(new Vector3f(-1f, -1f, -1));
        allEncompassingNode.addLight(dl);
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White.mult(0.4f));
        dl2.setDirection(new Vector3f(1f, 1f, 1));
        allEncompassingNode.addLight(dl2);
        
//Constructor SprinkleObjects(AssetManager newAssetManager, Camera cam, int treasurePointValue, int maxPointsInArea, 
//            int minDistanceToPlayer, int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
        sprinkler = new SprinkleObjects(assetManager, cam, GEMVALUE, MAXSCORE, 10, 8, 30, 80, 70);
        sprinkleNode = sprinkler.sprinkle();
        gObjectsList = sprinkler.getGOList();
        sceneNode.attachChild(sprinkleNode);
        //sprinkleNode.setShadowMode(ShadowMode.CastAndReceive);

        allEncompassingNode.attachChild(sceneNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));

        addToWorld();

        //temp add lights
        //addLights();
        //addToWorld();
        
        //More confortable flycam and disable
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setEnabled(false);

        player = new Player(assetManager, bulletAppState, allEncompassingNode, cam, sprinkler.getPlayer().getWorldTranslation());
        sprinkleNode.detachChild(sprinkler.getPlayer());
        //player.getNode().setShadowMode(ShadowMode.Receive);

        score = 0;
        
        //TEST SHADOWS
        /**
        player.getNode().setShadowMode(ShadowMode.Inherit);   
        rootNode.setShadowMode(ShadowMode.Off);    
        player.getNode().setShadowMode(ShadowMode.Cast);
        maze.getPlane().setShadowMode(ShadowMode.Receive);
        
        final int SHADOWMAP_SIZE=1024;
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(dl2);
        viewPort.addProcessor(dlsr);
        **/
        
        rootNode.attachChild(allEncompassingNode);
    }

    private void restartGame() {
        bulletAppState.getPhysicsSpace().removeAll(rootNode);
        rootNode.detachAllChildren();
        System.out.println("Restart");
        initGame();
    }

    private void addToWorld() {
        for (GameObject gObject : gObjectsList) {

            if (gObject.getCName().equals("Flower pot")) {
                PointLight lamp_light = new PointLight();
                lamp_light.setColor(ColorRGBA.Orange.mult(ColorRGBA.Yellow));
                lamp_light.setRadius(8f);
                lamp_light.setPosition(gObject.getWorldTranslation().add(0, 0.55f, 0));
                makeCube(gObject.getWorldTranslation().add(0, 0.55f, 0));
                allEncompassingNode.addLight(lamp_light);
                lightList.add(lamp_light);
                //renderShadows(allEncompassingNode, lamp_light, ShadowMode.CastAndReceive);
            }

            if (gObject.getCName().equals("Desk")) {
                RigidBodyControl cratePhy = new RigidBodyControl(0f);
                gObject.getGeom().addControl(cratePhy);
                bulletAppState.getPhysicsSpace().add(cratePhy);
            }
            
            if (gObject.getCName().equals("Objective")) {

                PointLight lamp_light = new PointLight();
                lamp_light.setColor(ColorRGBA.Yellow.mult(0.8f));
                lamp_light.setRadius(10f);
                lamp_light.setPosition(new Vector3f(gObject.getWorldTranslation().add(0,1,0)));
                allEncompassingNode.addLight(lamp_light);
                lightList.add(lamp_light);
                //renderShadows(allEncompassingNode, lamp_light, ShadowMode.Cast);
            }
        }
    }

    //TEST Method
    private void makeCube(Vector3f loc) {
        Material objectMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        objectMat.setColor("Color", ColorRGBA.Orange.mult(0.8f));
        Box objectiveBox = new Box(0.05f, 0.05f, 0.05f);
        Geometry object = new Geometry("Objective", objectiveBox);
        object.setMaterial(objectMat);
        object.setLocalTranslation(loc);
        rootNode.attachChild(object);
    }
}
