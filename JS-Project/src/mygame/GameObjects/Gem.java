/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author José Castanheira
 */
public final class Gem extends GameObject {

    @Override
    public String getCName() {
        return "Gem";
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture gemText = assetManager.loadTexture("gem.png");
        gemText.setWrap(Texture.WrapMode.Repeat);
        objectMat.setTexture("DiffuseMap", gemText);
        objectMat.setBoolean("UseMaterialColors", true);
        objectMat.setColor("Diffuse", ColorRGBA.White);  // minimum material color
        objectMat.setColor("Specular", ColorRGBA.White); // for shininess
        objectMat.setFloat("Shininess", 120f); // [1,128] for shininess
        
    }

    @Override
    protected void loadPhysicsModel() {
        Sphere gemS = new Sphere(32,32, 0.25f);
        object = new Geometry("Gem", gemS);
        gemS.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(gemS);
        object.setMaterial(objectMat);
        object.rotateUpTo(new Vector3f(0.5f, 0.5f, 0.5f));
       
    }

    public Gem(AssetManager assetManager) {
       
        this.assetManager = assetManager;
        createMaterial();
        loadPhysicsModel();

        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f); 
        gameObjectNode.attachChild(object);
        
        PointLight lamp_light = new PointLight();
                lamp_light.setColor(ColorRGBA.Red.mult(4f));
                lamp_light.setRadius(200f);
                lamp_light.setPosition(new Vector3f(getLocation()));
                gameObjectNode.addLight(lamp_light);
    }

    @Override
    public void update(float tpf) {
        object.rotate(0, 2 * tpf, 0);
    }
}
