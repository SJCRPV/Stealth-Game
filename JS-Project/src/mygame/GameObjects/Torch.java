/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 *
 * @author SJCRPV
 */
public final class Torch extends StandardObject {

    ParticleEmitter fire;
    Material fireMat;
    AudioNode aTorch;
    
    @Override
    public String getClassName()
    {
        return "Torch";
    }
    
    public AudioNode getAudioNode()
    {
        return aTorch;
    }
    
    private void loadParticles()
    {
        //TODO: Make it more convincing. The fire specks aren't jumping out of the torch, they're being shoved out.
        ColorRGBA startColour = new ColorRGBA(0.8f, 0.8f, 0f, 0.5f);
        ColorRGBA endColour = new ColorRGBA(0.6f, 0f, 0.1f, 1f);
        
        fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 20);
        fire.setShape(new EmitterPointShape(Vector3f.ZERO));
        fire.setLocalTranslation(object.getLocalTranslation().add(new Vector3f(0, 0, 0.55f)));
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setStartColor(startColour);
        fire.setEndColor(endColour);
        fire.setStartSize(0.25f);
        fire.setEndSize(0.06f);
        fire.setLowLife(1f);
        fire.setHighLife(3f);
        fire.setParticlesPerSec(12);
        fire.setGravity(0, 0, 0);
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0,0,0.3f));
        fire.getParticleInfluencer().setVelocityVariation(0.01f);
        
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
    public boolean handleCollisions(GameObject collider) 
    {
        return false;
    }
    
    @Override
    public void loadPhysics()
    {
    }
    
    public void loadAudio()
    {
        //Potential problem. Only one will play sounds.
        aTorch = new AudioNode(assetManager, "Sounds/fire.ogg", AudioData.DataType.Stream);
        aTorch.setLooping(true);
        aTorch.setPositional(true);
        aTorch.setLocalTranslation(object.getWorldTranslation().add(0, 0.55f, 0));
        aTorch.setVolume(80);
        aTorch.setPitch(0.8f);
        aTorch.setRefDistance(0.1f);
        aTorch.setMaxDistance(2000f);
        aTorch.play();
    }
    
    @Override
    protected void loadModel() {
        Box flowerPotBox = new Box(0.05f, 0.05f, 0.5f);
        object = new Geometry("Flower Pot", flowerPotBox);
        object.setMaterial(objectMat);
        object.setCullHint(Spatial.CullHint.Dynamic);
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Torch(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
//        loadAudio();
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
