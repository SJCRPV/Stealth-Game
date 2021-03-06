/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author SJCRPV
 */
public abstract class GameObject extends AbstractAppState {
    
    protected Node gameObjectNode = new Node();
    
    protected Spatial object;
    protected RigidBodyControl objectPhy;
    protected AssetManager assetManager;
    protected Material objectMat;
    protected Vector3f objectDimensions;
    protected int placedAtX;
    protected int placedAtY;
    protected int numOfSubCellsItOccupies;
    
    protected abstract void createMaterial();
    protected abstract void loadModel();
    protected abstract GameObject getGObjectClone();
    public abstract void loadPhysics();
    public abstract String getClassName();
    public abstract boolean handleCollisions(GameObject collider);
    
    protected void defineObjectBounds() 
    {
        BoundingBox bb = (BoundingBox)object.getWorldBound();
        bb.getExtent(objectDimensions);
    }
    
    public int[] getCellCoordinates()
    {
        return new int[] {placedAtX, placedAtY};
    }
    public void setCellCoordinates(int x, int y)
    {
        placedAtX = x;
        placedAtY = y;
    }
    
    public Vector3f getWorldTranslation()
    {
        return gameObjectNode.getWorldTranslation();
    }
    public Vector3f getLocalTranslation()
    {
        return gameObjectNode.getLocalTranslation();
    }
    public void setLocalTranslation(Vector3f location)
    {
        gameObjectNode.setLocalTranslation(location);
    }
    
    public Vector3f getObjectDimensions()
    {
        return objectDimensions;
    }
    
    public Spatial getSpatial()
    {
        return object;
    }
    
    public Node getNode()
    {
        return gameObjectNode;
    }
    
    public Geometry getGeom()
    {
        return (Geometry)object;
    }
    
    public RigidBodyControl getRb()
    {
        return objectPhy;
    }
}
