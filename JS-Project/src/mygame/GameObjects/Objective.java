/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author SJCRPV
 */
public final class Objective extends GameObject {

    @Override
    public String getCName()
    {
        return "Objective";
    }
    
    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        objectMat.setColor("Color", ColorRGBA.White);
    }

    @Override
    protected void loadPhysicsModel() {
        Box objectiveBox = new Box(0.25f, 0.25f, 0.25f);
        object = new Geometry("Objective", objectiveBox);
        object.setMaterial(objectMat);
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Objective(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadPhysicsModel();
        defineObjectBounds();
        
        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f);
    }
    
    @Override
    public void update(float tpf)
    {
    }
}