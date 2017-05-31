/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author José Castanheira
 */
public final class Gem extends GameObject {

    Material sparkleMat;
    ParticleEmitter sparkles;
    
    private final static int GEMVALUE = 50;
    
    @Override
    public String getClassName() {
        return "Gem";
    }
    
    public static int getGemValue()
    {
        return GEMVALUE;
    }

    @Override
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void loadParticles()
    {
        Geometry g = (Geometry)object;
        Sphere s = (Sphere)g.getMesh();
        ColorRGBA startColour = new ColorRGBA(0.843f, 0.531f, 0.684f, 1f);
        ColorRGBA endColour = new ColorRGBA(0.98f, 0.631f, 0.91f, 1f);
        
        sparkles = new ParticleEmitter("Sparkles", ParticleMesh.Type.Triangle, 2);
        sparkles.setShape(new EmitterSphereShape(Vector3f.ZERO, s.getRadius() + 0.2f));
        sparkles.setLocalTranslation(object.getLocalTranslation());
        sparkles.setImagesX(2);
        sparkles.setImagesY(2);
        sparkles.setStartColor(startColour);
        sparkles.setEndColor(endColour);
        sparkles.setStartSize(0.1f);
        sparkles.setEndSize(0.01f);
        sparkles.setGravity(0,0,0);
        sparkles.setLowLife(0.3f);
        sparkles.setHighLife(0.6f);
        sparkles.setParticlesPerSec(1);
        
        sparkles.setMaterial(sparkleMat);
        
        gameObjectNode.attachChild(sparkles);
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
        
        sparkleMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        sparkleMat.setTexture("Texture", assetManager.loadTexture("flash.png"));
    }

    @Override
    public boolean handleCollisions(GameObject collider)
    {
        Player player = (Player)collider;
        CollisionResults results = new CollisionResults();
        BoundingVolume bv = this.getGeom().getWorldBound();
        player.getSpatial().collideWith(bv, results);

        if (results.size() > 0) 
        {
            player.addToScore(GEMVALUE);
            System.out.println(player.getScore());
            return true;
        }
        return false;
    }
    
    @Override
    public void loadPhysics()
    {
    }
    
    @Override
    protected void loadModel() {
        Sphere gemS = new Sphere(32, 32, 0.25f);
        object = new Geometry("Gem", gemS);
        object.setMaterial(objectMat);
        gemS.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(gemS);
        object.setCullHint(Spatial.CullHint.Dynamic);
    }

    public Gem(AssetManager assetManager) {
       
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        loadParticles();

        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f); 
        gameObjectNode.attachChild(object);
        
        PointLight lamp_light = new PointLight();
        lamp_light.setColor(ColorRGBA.Red.mult(4f));
        lamp_light.setRadius(200f);
        lamp_light.setPosition(new Vector3f(getWorldTranslation()));
        gameObjectNode.addLight(lamp_light);
    }

    @Override
    public void update(float tpf) {
        object.rotate(0, 2 * tpf, 0);
        sparkles.emitParticles((int)sparkles.getParticlesPerSec());
    }
}
