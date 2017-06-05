/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author SJCRPV
 */
public final class Enemy extends GameObject implements AnimEventListener {
    
    private float direction;
    private Quaternion rotation = new Quaternion();
    private final Node mazeNode;
    private AnimChannel enemyChannel;
    private AnimControl control;
    private boolean stop = false;

    private final static float MIN_DIST = 1.2f;
    private final static float SPEED = 1f;
    private final static float ROT = 60 * FastMath.DEG_TO_RAD;

    @Override
    public String getClassName() 
    {
        return "Enemy";
    }
    
    @Override
    protected GameObject getGObjectClone() 
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) 
    {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) 
    {   
    }
    
    private void setAnimationControl() 
    {
        control = object.getControl(AnimControl.class);
        control.addListener(this);
        enemyChannel = control.createChannel();
        enemyChannel.setAnim("Walk",0.5f);
        enemyChannel.setLoopMode(LoopMode.Cycle);
    }
    
    private void defineLighting() 
    {
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(object.getLocalTranslation());               // shine from camera loc
        spot.setDirection(new Vector3f(0, -1, 0));             // shine forward from camera loc
        gameObjectNode.addLight(spot);
    }
    
    @Override
    public boolean handleCollisions(GameObject collider)
    {
        Player player = (Player) collider;
        CollisionResults results = new CollisionResults();
        BoundingVolume bv = new BoundingBox(object.getWorldTranslation(),0.2f,0.2f,0.2f);
       
        player.getSpatial().collideWith(bv, results);

        return results.size() > 0;
    }
    
    @Override
    public void loadPhysics() 
    {
    }
    
    @Override
    protected final void loadModel() 
    {
        object = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        object.rotateUpTo(new Vector3f(0, 0, 1));
        object.rotate(0, FastMath.PI / 2, 0);
        object.scale(0.15f);
        object.setLocalTranslation(0, 0, 0.2f);
        object.setCullHint(Spatial.CullHint.Dynamic);
    }
    
    @Override
    protected final void createMaterial() 
    {
//        objectMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        objectMat.setColor("Color", ColorRGBA.Brown);
    }
    
    public Enemy(AssetManager assetManager, Node mazeNode) 
    {
        this.assetManager = assetManager;
        this.mazeNode = mazeNode;

        createMaterial();
        loadModel();
        loadPhysics();
        defineObjectBounds();
        defineLighting();
        setAnimationControl();

        direction = (float) (Math.random() * 2 * FastMath.PI);
        //direction = FastMath.PI /2;

        //Temp
        objectDimensions = new Vector3f(0.25f, 0.25f, 0.5f);

        gameObjectNode.attachChild(object);
    }
    
    public void stop()
    {
        stop = true;
        enemyChannel.setAnim("push",0);
        enemyChannel.setLoopMode(LoopMode.Cycle);
    }
    
//    private Vector3f reflectDirection(Vector3f vector, Vector3f colliderNormal)
//    {
//        //Formula:  vector - 2 * vector.dot(colliderNormal) * colliderNormal
//        return vector.subtract(colliderNormal.mult(vector.dot(colliderNormal)).mult(2));
//    }
    
    private void castRay(float tpf)
    {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(gameObjectNode.getWorldTranslation().add(0, 1, 0), new Vector3f(FastMath.cos(direction), 0, -FastMath.sin(direction)));
        mazeNode.collideWith(ray, results);

        if (results.size() > 0) 
        {
            CollisionResult closest = results.getClosestCollision();
            if (closest.getDistance() < MIN_DIST) 
            {
                //Em vez de rodar 90º, se calhar é melhor apenas dar uma direção simétrica?
                direction += ROT;
                //direction = Vector3f.ZERO.angleBetween(new Vector3f(FastMath.cos(direction),FastMath.sin(direction), 0));
            }
        }

        rotation.fromAngles(0, 0, direction);
        gameObjectNode.setLocalRotation(rotation);
        gameObjectNode.move(tpf * SPEED * FastMath.cos(direction), tpf * SPEED * FastMath.sin(direction), 0);
    }
    
    @Override
    public void update(float tpf) 
    {
        if(!stop)
        {
            castRay(tpf);
        }
        /**
         * CollisionResults results = new CollisionResults(); Ray ray = new
         * Ray(gameObjectNode.getWorldTranslation(), new Vector3f(0,0,1*speed));
         * mazeNode.collideWith(ray, results); // 5. Use the results (we mark
         * the hit object) if (results.size() > 0) { // The closest collision
         * point is what was truly hit: CollisionResult closest =
         * results.getClosestCollision();
         * System.out.println(closest.getDistance());
         * if(closest.getDistance()<MIN_DIST) speed = -speed; } 
        */
    }
}
