/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author Castanheira
 */

import com.jme3.asset.AssetManager;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;

public class Player {
    
    //Player variables
    protected static float ROTATIONSPEED = 0.01f;
    protected static float WALKSPEED = 0.15f;
    
    private final CharacterControl physicsCharacter;
    private final Node characterNode;
    private final CameraNode camNode;
    boolean rotate = false;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 0);
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    
    private final Camera cam;
    
    public Player(AssetManager assetManager, Node rootNode, Camera cam,Vector3f startPos)
    {
        this.cam = cam;
        
        // Add a physics character to the world
        physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.4f, 0.6f), .1f);
        physicsCharacter.setPhysicsLocation(new Vector3f(0, 1, 0));
        characterNode = new Node("character node");
        Spatial model = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        model.scale(0.15f);
        //Temp
        Material whitemat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        whitemat.setColor("Color", ColorRGBA.White);
        model.setMaterial(whitemat);
        characterNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);
        physicsCharacter.setPhysicsLocation(startPos); //Start position in the game
        rootNode.attachChild(characterNode);
        characterNode.attachChild(model);

        // set forward camera node that follows the character
        camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 1, -5));
        camNode.lookAt(model.getLocalTranslation(), Vector3f.UNIT_Y);
        characterNode.attachChild(camNode);
    }
    
    public void detachCamera()
    {
        characterNode.detachChild(camNode);
    }
    
    public void attachCamera()
    {
        characterNode.attachChild(camNode);
    }
    
    public void move()
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
                viewDirection.addLocal(camLeft.mult(ROTATIONSPEED));
            } else if (rightRotate) {
                viewDirection.addLocal(camLeft.mult(ROTATIONSPEED).negate());
            }
            if (forward) {
                walkDirection.addLocal(camDir);
            } else if (backward) {
                walkDirection.addLocal(camDir.negate());
            }
            physicsCharacter.setWalkDirection(walkDirection);
            physicsCharacter.setViewDirection(viewDirection);
    }
    public void controls(String name, boolean keyPressed)
    {
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
    
    public Vector3f getLocation()
    {
        return physicsCharacter.getPhysicsLocation();
    }
}


