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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author SJCRPV
 */
public final class Crate extends StandardObject {

    
    @Override
    public String getClassName() {
        return "Crate";
    }

    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture crateText = assetManager.loadTexture("crate1_diffuse.png");
        crateText.setWrap(Texture.WrapMode.Repeat);
        objectMat.setTexture("DiffuseMap", crateText);
        Texture crateNormal = assetManager.loadTexture("crate1_normal.png");
        crateNormal.setWrap(Texture.WrapMode.Repeat);
        objectMat.setTexture("NormalMap", crateNormal);
        objectMat.setBoolean("UseMaterialColors", true);
        objectMat.setColor("Diffuse", ColorRGBA.White);  // minimum material color
        objectMat.setColor("Specular", ColorRGBA.White); // for shininess
        objectMat.setFloat("Shininess", 128f); // [1,128] for shininess
    }
    
    @Override
    public boolean handleCollisions(GameObject collider) 
    {
        return false;
    }

    @Override
    public void loadPhysics()
    {
    }
    
    @Override
    protected void loadModel() {
        Box computerDeskBox = new Box(0.25f, 0.25f, 0.25f);
        object = new Geometry("Computer Desk", computerDeskBox);
        TangentBinormalGenerator.generate(computerDeskBox);
        object.setMaterial(objectMat);
        object.rotate(0,0,(float) (Math.random() * Math.PI));
        object.setCullHint(Spatial.CullHint.Dynamic);
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Crate(AssetManager assetManager) {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        defineObjectBounds();

        //Temp
        objectDimensions = new Vector3f(0.25f, 0.25f, 0.25f);

        gameObjectNode.attachChild(object);
    }

    @Override
    public void update(float tpf) 
    {
    }
}
