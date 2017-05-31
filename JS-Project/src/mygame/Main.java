package mygame;

import mygame.MapGeneration.RecDivMazeGrid;
import mygame.MapGeneration.SprinkleObjects;
import mygame.GameObjects.Player;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;
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
    
    private static final int MAXSCORE = 1000;
    private static final int SHADOWMAP_SIZE = 1024;
    
    RecDivMazeGrid maze;
    SprinkleObjects sprinkler;
    Player player;
    
    Node sceneNode;
    Node sprinkleNode;
    Node allEncompassingNode;

    private BulletAppState bulletAppState;

    private boolean freeCam = false;
    private List<GameObject> gObjectsList;
    private List<PointLight> lightList;

    Material sparkleMat;
    ParticleEmitter sparkles;
    
    private void renderShadows(Node relevantNode, Light light, ShadowMode shadowMode)
    {
        //WARNING: Se calhar não é preciso fazer shadows das luzes direcionais que iluminam o mapa em geral. Se calhar, apenas
        //as luzes criadas pelas tochas e afins chegam.
        relevantNode.setShadowMode(shadowMode);
        
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
    
    private void createLight(GameObject gObject, ColorRGBA colour, float lightRadius, Vector3f position, ShadowMode shadowMode)
    {
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(colour);
        lamp_light.setRadius(lightRadius);
        lamp_light.setPosition(gObject.getWorldTranslation().add(position));
        //akeCube(gObject.getWorldTranslation().add(0, 0.55f, 0));
        allEncompassingNode.addLight(lamp_light);
        lightList.add(lamp_light);
        //renderShadows(allEncompassingNode, lamp_light, shadowMode);
    }
    
    private void addToWorld() 
    {
        for (GameObject gObject : gObjectsList) 
        {
            if (gObject.getClassName().equals("Flower pot")) 
            {
                createLight(gObject, ColorRGBA.Orange.mult(ColorRGBA.Yellow), 8f, new Vector3f(0, 0.55f, 0), ShadowMode.CastAndReceive);
            }

            if (gObject.getClassName().equals("Desk")) 
            {
                RigidBodyControl cratePhy = new RigidBodyControl(0f);
                gObject.getGeom().addControl(cratePhy);
                bulletAppState.getPhysicsSpace().add(cratePhy);
            }
            
            if (gObject.getClassName().equals("Objective")) 
            {
                createLight(gObject, ColorRGBA.Yellow.mult(0.8f), 10f, Vector3f.UNIT_Y, ShadowMode.Cast);
            }
        }
    }
    
    private void setFlyCamSettings()
    {
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setEnabled(false);
    }
    
    private void lightScene()
    {
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(0.4f));
        dl.setDirection(new Vector3f(-1f, -1f, -1));
        allEncompassingNode.addLight(dl);
        DirectionalLight dl2 = new DirectionalLight();
        dl2.setColor(ColorRGBA.White.mult(0.4f));
        dl2.setDirection(new Vector3f(1f, 1f, 1));
        allEncompassingNode.addLight(dl2);
    }
    
    private Node prepareSprinkleNode()
    {
//Constructor SprinkleObjects(AssetManager newAssetManager, Camera cam, int treasurePointValue, int maxPointsInArea, 
//            int minDistanceToPlayer, int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
        sprinkler = new SprinkleObjects(assetManager, cam, Gem.getGemValue(), MAXSCORE, 10, 8, 30, 80, 70);
        Node node = new Node();
        node.attachChild(sprinkler.sprinkle());
        //node.setShadowMode(ShadowMode.CastAndReceive);
        
        return node;
    }
    
    private Node prepareMazeNode()
    {
//Constructor RecDivMazeGrid(AssetManager newAssetManager, int numCellsWide, int numCellsTall, float cellWidth, float cellHeight, 
//        float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall)
        maze = new RecDivMazeGrid(assetManager, bulletAppState, 15, 15, 1f, 1f, 0.5f, 1, 4, 4);
        Node node = new Node();
        node.attachChild(maze.generateMaze());
        node.setShadowMode(ShadowMode.Cast);
        
        return node;
    }
    
    private void initGame() 
    {
        sceneNode = prepareMazeNode();
        
        allEncompassingNode = new Node("newRoot");
        
        lightList = new ArrayList();
                
        lightScene();
        
        sprinkleNode = prepareSprinkleNode();

        gObjectsList = sprinkler.getGOList();
        
        sceneNode.attachChild(sprinkleNode);
        sceneNode.rotateUpTo(new Vector3f(0, 0, -1));
        allEncompassingNode.attachChild(sceneNode);
        
        addToWorld();
        
        //More confortable flycam and disable
        setFlyCamSettings();

        player = new Player(assetManager, bulletAppState, allEncompassingNode, cam, sprinkler.getPlayer().getWorldTranslation());
        sprinkleNode.detachChild(sprinkler.getPlayer());
        //player.getNode().setShadowMode(ShadowMode.Receive);
        
        rootNode.attachChild(allEncompassingNode);
    }
    
    private void initKeys() 
    {
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
    
    private void switchCamera()
    {
        if (!freeCam) 
        {
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
        } 
        else 
        {
            //Disable flycam
            flyCam.setEnabled(false);
            //Attach chase camera
            player.attachCamera();
            freeCam = false;
        }
    }
    
    private void restartGame() 
    {
        bulletAppState.getPhysicsSpace().removeAll(rootNode);
        rootNode.detachAllChildren();
        System.out.println("Restart");
        initGame();
    }
    
    private ActionListener actionListener = new ActionListener() 
    {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) 
        {
            //Restart maze (temp)
            if (name.equals("Restart") && !keyPressed && !freeCam) 
            {
                restartGame();
            }

            if (name.equals("Camera") && !keyPressed) 
            {
                switchCamera();
            }

            //Player controls
            player.controls(name, keyPressed);
        }
    };
    
    @Override
    public void simpleInitApp() 
    {
        //To avoid not showing objects behind player. Does not work well with flycam
        cam.setFrustumPerspective(45, settings.getWidth() / settings.getHeight(), 0.0001f, 1000f);

        //Activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        //initKeys is only here for testing individual systems.
        initKeys();
        initGame();
    }

    public static void main(String[] args)
    {
        Main app = new Main();
        app.start();
    }
    
    private void handleCollisions(GameObject gObject)
    {
        if(gObject.handleCollisions(player))
        {
            if (gObject.getClassName().equals("Objective")) 
            {
                restartGame();
            }

            if (gObject.getClassName().equals("Gem")) 
            {
                loadGemParticles(gObject);
                gObject.getNode().removeFromParent();
            }
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) 
    {
        if (!freeCam)
        {
            player.move(tpf);
        }
        for (GameObject gObject : gObjectsList) 
        {
            gObject.update(tpf);

            //For some reason, the gems no longer disappear.
            handleCollisions(gObject);
        }
        
        for (PointLight light : lightList) 
        {
            light.setRadius(8 + (float) (Math.random()));
        }
    }
    
    private void loadGemParticles(GameObject g)
    {   
        Sphere s = (Sphere) g.getGeom().getMesh();
        
        sparkleMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        sparkleMat.setTexture("Texture", assetManager.loadTexture("flash.png"));
        
        ColorRGBA startColour = new ColorRGBA(0.843f, 0.531f, 0.684f, 1f);
        ColorRGBA endColour = new ColorRGBA(0.98f, 0.631f, 0.91f, 1f);
        
        sparkles = new ParticleEmitter("Sparkles", ParticleMesh.Type.Triangle, 20);
        sparkles.setShape(new EmitterPointShape(Vector3f.ZERO));
        sparkles.setLocalTranslation(g.getSpatial().getWorldTranslation());
        sparkles.setImagesX(2);
        sparkles.setImagesY(2);
        sparkles.setStartColor(startColour);
        sparkles.setEndColor(endColour);
        sparkles.setStartSize(0.4f);
        sparkles.setEndSize(0.01f);
        sparkles.setGravity(0,0,0);
        sparkles.setLowLife(0.3f);
        sparkles.setHighLife(0.6f);
        sparkles.setParticlesPerSec(0);
        sparkles.setRandomAngle(false);
        sparkles.setGravity(0, 6, 0);
        sparkles.getParticleInfluencer().setInitialVelocity(new Vector3f(0,4,0));
        sparkles.getParticleInfluencer().setVelocityVariation(0.65f);
        
        sparkles.setMaterial(sparkleMat);
        rootNode.attachChild(sparkles);  
        sparkles.emitAllParticles();
    }
}
