/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


/**
 *
 * @author SJCRPV
 */
public final class Enemy extends GameObject {

    @Override
    public String getClassName()
    {
        return "Enemy";
    }
    
    @Override
    protected final void createMaterial()
    {
        objectMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        objectMat.setColor("Color", ColorRGBA.Brown);
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
    protected final void loadModel()
    {
        object = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        object.rotateUpTo(new Vector3f(0,0,1));
        object.scale(0.15f);
        object.setCullHint(Spatial.CullHint.Dynamic);
    }
    
    private void defineLighting()
    {
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.Red.mult(1.3f));         // light color
        spot.setPosition(object.getLocalTranslation().add(new Vector3f(0, 10f, 10f)));               // shine from camera loc
        //spot.setDirection(Vector3f.UNIT_Z.mult(-1));             // shine forward from camera loc
        object.addLight(spot);
    }
    
    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Enemy(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        defineObjectBounds();
        defineLighting();
        
        //Temp
        objectDimensions = new Vector3f(0.25f, 0.25f, 0.5f);
        
        SpotLight spot = new SpotLight();
        spot.setSpotRange(100f);                           // distance
        spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone (central beam)
        spot.setSpotOuterAngle(35f * FastMath.DEG_TO_RAD); // outer light cone (edge of the light)
        spot.setColor(ColorRGBA.White.mult(1.3f));         // light color
        spot.setPosition(object.getLocalTranslation());               // shine from camera loc
        spot.setDirection(new Vector3f(0,-1,0));             // shine forward from camera loc
        gameObjectNode.addLight(spot);
        
        gameObjectNode.attachChild(object);
    }

    @Override
    public void update(float tpf)
    {
    }
}
