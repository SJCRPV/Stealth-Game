/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

/**
 *
 * @author Castanheira
 */
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;

public final class Player extends GameObject implements AnimEventListener {

    //Player variables
    protected static float ROTATIONSPEED = 3f;
    protected static float WALKSPEED = 0.1f;
    protected static float JUMPSPEED = 8;
    protected static String JUMPS = "JumpStart";
    protected static String JUMPE = "JumpEnd";
    protected static String JUMP = "JumpLoop";
    protected static String IDLET = "IdleTop";
    protected static String IDLEB = "IdleBase";
    protected static String RUNT = "RunTop";
    protected static String RUNB = "RunBase";

    private BulletAppState bulletAppState;
    private CharacterControl physicsCharacter;
    private CameraNode camNode;
    boolean rotate = false;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;

    private Camera cam;

    private AnimChannel topChannel;
    private AnimChannel botChannel;
    private AnimControl control;

    @Override
    public String getCName() {
        return "Player";
    }
    
    public void detachCamera() 
    {
        gameObjectNode.detachChild(camNode);
    }

    public void attachCamera() 
    {
        gameObjectNode.attachChild(camNode);
    }

    public void setPhysicsRotation(Vector3f rotation)
    {
        gameObjectNode.rotate(rotation.x, rotation.y, rotation.z);
    }
    
    public void setPhysicsLocation(Vector3f location)
    {
        physicsCharacter.setPhysicsLocation(location);
    }

    public Vector3f getPhysicsLocationLocation() 
    {
        return physicsCharacter.getPhysicsLocation();
    }

    private void placeCharacter(Node rootNode, Vector3f startPos) 
    {
        gameObjectNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);
        physicsCharacter.setPhysicsLocation(startPos); //Start position in the game
        rootNode.attachChild(gameObjectNode);
        gameObjectNode.attachChild(object);
     }
    
    public void setFollowingCameraNode(Camera cam) 
    {
        this.cam = cam;
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 0.5f, -2f)); //Best 0,0.5,-2
        camNode.lookAt(object.getLocalTranslation(), Vector3f.UNIT_Y);
        gameObjectNode.attachChild(camNode);
    }

    @Override
    public void loadPhysics()
    {
        physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.2f, 0.5f), .1f);
        physicsCharacter.setPhysicsLocation(new Vector3f(0, 1, 0));
        gameObjectNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);
    }

    @Override
    protected void loadModel()
    {
        object = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        object.scale(0.1f);

        gameObjectNode.attachChild(object);
    }

    @Override
    protected void createMaterial() {
        /**
         * Temp Material whitemat = new Material(assetManager,
         * "Common/MatDefs/Misc/Unshaded.j3md"); whitemat.setColor("Color",
         * ColorRGBA.White); model.setMaterial(whitemat);*
         */
    }

    private void setAnimationControl() {
        control = object.getControl(AnimControl.class);
        control.addListener(this);
        botChannel = control.createChannel();
        topChannel = control.createChannel();
        botChannel.setAnim(IDLEB, 0.5f);
        topChannel.setAnim(IDLET, 0.5f);
        topChannel.setLoopMode(LoopMode.Cycle);
        botChannel.setLoopMode(LoopMode.Cycle);
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (botChannel.getAnimationName().equals(JUMPE)) {
            botChannel.setAnim(IDLEB, 0.5f); //second parameter important for character feel
            topChannel.setAnim(IDLET, 0.5f); //second parameter important for character feel
            botChannel.setLoopMode(LoopMode.Cycle);
        }

        if (botChannel.getAnimationName().equals(JUMPS)) {
            botChannel.setAnim(JUMP, 0.1f); //second parameter important for character feel
            botChannel.setLoopMode(LoopMode.Cycle);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {

    }

    public void stop() {
        leftStrafe = false;
        rightStrafe = false;
        leftRotate = false;
        rightRotate = false;
        forward = false;
        backward = false;
        walkDirection.set(0, 0, 0);
        viewDirection.set(0, 0, 0);
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
        botChannel.setAnim(IDLEB, 0.5f);
        topChannel.setAnim(IDLET, 0.5f);
    }

    public void move(float tpf) 
    {
        Vector3f camDir = cam.getDirection().mult(WALKSPEED);
        Vector3f camLeft = cam.getLeft().mult(WALKSPEED);
        camDir.y = 0;
        camLeft.y = 0;
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
        if (leftStrafe) {
            walkDirection.addLocal(camLeft);
        } else if (rightStrafe) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (leftRotate) {
            viewDirection.addLocal(camLeft.mult(ROTATIONSPEED * tpf));
        } else if (rightRotate) {
            viewDirection.addLocal(camLeft.mult(ROTATIONSPEED * tpf).negate());
        }
        if (forward) {
            walkDirection.addLocal(camDir);
        } else if (backward) {
            walkDirection.addLocal(camDir.negate());
        }

        if (physicsCharacter.onGround()) {

            if (botChannel.getAnimationName().equals(JUMP)) {
                botChannel.setAnim(JUMPE, 0); //second parameter important for character feel
                botChannel.setLoopMode(LoopMode.DontLoop);
            }

            //Buttons
            if (leftRotate || rightRotate || backward || forward || leftStrafe || rightStrafe) {
                if (!botChannel.getAnimationName().equals(RUNB)) {
                    botChannel.setAnim(RUNB, 0.1f); //second parameter important for character feel
                }

                //Turn on and off top part for more natural animations
                if (!topChannel.getAnimationName().equals(RUNT) && (backward || forward || leftStrafe || rightStrafe)) {
                    topChannel.setAnim(RUNT, 0.5f);
                }

                if (topChannel.getAnimationName().equals(RUNT) && (!backward && !forward && !leftStrafe && !rightStrafe)) {
                    topChannel.setAnim(IDLET, 0.4f);
                }

                if (!backward && !forward && !leftStrafe && !rightStrafe) //Animation speed
                {
                    botChannel.setSpeed(0.6f);
                } else {
                    botChannel.setSpeed(1);
                }

            } else {
                if (botChannel.getAnimationName().equals(RUNB)) {
                    botChannel.setAnim(IDLEB, 0.1f);
                    topChannel.setAnim(IDLET, 0.4f);
                }
            }
        } else {
            if (!botChannel.getAnimationName().equals(JUMPS) && !botChannel.getAnimationName().equals(JUMP) && !botChannel.getAnimationName().equals(JUMPE)) {
                botChannel.setAnim(JUMPS, 0.1f); //second parameter important for character feel
                botChannel.setLoopMode(LoopMode.DontLoop);
            }
        }

        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
    }

    public void controls(String name, boolean keyPressed) {
        if (name.equals("Strafe Left")) {
            if (keyPressed) {
                leftStrafe = true;
            } else {
                leftStrafe = false;
            }
        } else if (name.equals("Strafe Right")) {
            if (keyPressed) {
                rightStrafe = true;
            } else {
                rightStrafe = false;
            }
        } else if (name.equals("Rotate Left")) {
            if (keyPressed) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (name.equals("Rotate Right")) {
            if (keyPressed) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (name.equals("Walk Forward")) {
            if (keyPressed) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (name.equals("Walk Backward")) {
            if (keyPressed) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (name.equals("Jump")) {
            physicsCharacter.jump();
            
        }
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Player(AssetManager assetManager, BulletAppState bulletAppState, Node rootNode, Camera cam, Vector3f startPos) {
        this.cam = cam;
        this.assetManager = assetManager;
        this.bulletAppState = bulletAppState;
        
        
        createMaterial();
        loadModel();
        loadPhysics();
        placeCharacter(rootNode, startPos);
        setFollowingCameraNode(cam);
        setAnimationControl();
        physicsCharacter.setJumpSpeed(JUMPSPEED);
    }
//    public Player(AssetManager assetManager, BulletAppState bulletAppState, Node rootNode, Camera cam, Vector3f startPos) {
//        this.cam = cam;
//        this.assetManager = assetManager;
//        this.bulletAppState = bulletAppState;
//        createMaterial();
//        super.loadModel();
//        placeCharacter(rootNode, startPos);
//        setFollowingCameraNode();
//        setAnimationControl();
//        
//        //Temp
//        objectDimensions = new Vector3f(0.4f, 1f, 1f);
//    }

    public Player(AssetManager assetManager) {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        loadPhysics();
        defineObjectBounds();
        setAnimationControl();
        
        //Temp
        objectDimensions = new Vector3f(0.4f, 1f, 1f);

        physicsCharacter.setJumpSpeed(JUMPSPEED);
    }

    public void setShadowMode(RenderQueue.ShadowMode shadowMode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
