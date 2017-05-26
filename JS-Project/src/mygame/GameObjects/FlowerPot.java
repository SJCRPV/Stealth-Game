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
import com.jme3.texture.Texture;

/**
 *
 * @author SJCRPV
 */
public final class FlowerPot extends StandardObject {

    @Override
    public String getCName()
    {
        return "Flower pot";
    }
    
    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture crateText = assetManager.loadTexture("153.JPG");
        objectMat.setTexture("DiffuseMap", crateText);
    }

    @Override
    public void loadPhysics()
    {
    }
    
    @Override
    protected void loadModel() {
        Box flowerPotBox = new Box(0.05f, 0.05f, 0.5f);
        object = new Geometry("Flower Pot", flowerPotBox);
        object.setMaterial(objectMat);
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public FlowerPot(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        defineObjectBounds();
        
        //Temp
        objectDimensions = new Vector3f(0.125f, 0.125f, 0.5f);
        
        gameObjectNode.attachChild(object);
    }
    
    @Override
    public void update(float tpf)
    {
    }
}
