/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterPointShape;
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

    ParticleEmitter fire;
    Material fireMat;
    
    @Override
    public String getCName()
    {
        return "Flower pot";
    }
    
    private void loadParticles()
    {
        //TODO: Make it more convincing. The fire specks aren't jumping out of the torch, they're being shoved out.
        Geometry g = (Geometry)object;
        Box s = (Box)g.getMesh();
        ColorRGBA startColour = new ColorRGBA(1f, 0.486f, 0.15f, 1f);
        ColorRGBA endColour = new ColorRGBA(0.98f, 0.831f, 0.91f, 1f);
        
        fire = new ParticleEmitter("Sparkles", ParticleMesh.Type.Triangle, 20);
        fire.setShape(new EmitterPointShape(Vector3f.ZERO));
        fire.setLocalTranslation(object.getLocalTranslation().add(new Vector3f(0, 0, 0.5f)));
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setStartColor(startColour);
        fire.setEndColor(endColour);
        fire.setStartSize(0.1f);
        fire.setEndSize(0.1f);
        fire.setLowLife(0.3f);
        fire.setHighLife(0.6f);
        fire.setParticlesPerSec(6);
        fire.setGravity(new Vector3f(1f, 1f, 1f));
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0.3f, 0.3f, 1));
        fire.getParticleInfluencer().setVelocityVariation(1f);
        
        fire.setMaterial(fireMat);
        
        gameObjectNode.attachChild(fire);
    }
    
    @Override
    protected void createMaterial() {
        objectMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture crateText = assetManager.loadTexture("153.JPG");
        objectMat.setTexture("DiffuseMap", crateText);
        
        fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
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
        loadParticles();
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
