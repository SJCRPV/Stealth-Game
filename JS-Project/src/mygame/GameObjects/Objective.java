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
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;

/**
 *
 * @author SJCRPV
 */
public final class Objective extends GameObject {

    private Material goldMat;
    private Geometry goldGeo;

    @Override
    public String getCName() {
        return "Objective";
    }

    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture chestText = assetManager.loadTexture("chestTexture.jpg");
        objectMat.setTexture("DiffuseMap", chestText);

        goldMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture goldText = assetManager.loadTexture("goldTexture.jpg");
        goldMat.setTexture("DiffuseMap", goldText);
    }

    @Override
    public void loadPhysics()
    {
    }
    
    @Override
    protected void loadModel() {
        Box objectiveBox = new Box(0.5f, 0.25f, 0.25f);
        object = new Geometry("Objective", objectiveBox);
        object.setLocalTranslation(0, 0, -0.26f);
        object.setMaterial(objectMat);
        
        Box goldBox = new Box(0.4f, 0.15f, 0.26f);
        goldGeo = new Geometry("Objective", goldBox);
        goldGeo.setLocalTranslation(0, 0, -0.26f);
        goldGeo.setMaterial(goldMat);
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Objective(AssetManager assetManager) {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        defineObjectBounds();

        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f);

        gameObjectNode.attachChild(object);
        gameObjectNode.attachChild(goldGeo);
    }

    @Override
    public void update(float tpf) {
    }
}
