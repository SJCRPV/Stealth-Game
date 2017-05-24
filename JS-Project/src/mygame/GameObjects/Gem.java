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
 * @author José Castanheira
 */
public final class Gem extends GameObject {

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md"); 
        objectMat.setColor("Color",ColorRGBA.Red); 
    }

    @Override
    protected void loadPhysicsModel() {
        Box gemBox = new Box(0.25f, 0.25f, 0.25f);
        object = new Geometry("Gem", gemBox);
        object.setMaterial(objectMat);
        object.rotateUpTo(new Vector3f(0.5f,0.5f,0.5f));
    }

    public Gem(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadPhysicsModel();
        
        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f);
    }

    @Override
    public void update(float tpf) 
    {
        object.rotate(0, 2*tpf, 0);
    }
}
