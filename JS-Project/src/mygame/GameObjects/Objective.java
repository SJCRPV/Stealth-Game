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
import com.jme3.effect.shapes.EmitterBoxShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author SJCRPV
 */
public final class Objective extends GameObject {

    private Material goldMat;
    private Geometry goldGeo;

    Material sparkleMat;
    ParticleEmitter sparkles;

    @Override
    public String getClassName() {
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

        sparkleMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        sparkleMat.setTexture("Texture", assetManager.loadTexture("flash.png"));
    }

    @Override
    public boolean handleCollisions(GameObject collider) {
        CollisionResults results = new CollisionResults();
        BoundingVolume bv = this.getGeom().getWorldBound();
        collider.getSpatial().collideWith(bv, results);

        if (results.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void loadPhysics() {
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
        object.setCullHint(Spatial.CullHint.Dynamic);
    }

    private void loadParticles() {
        ColorRGBA startColour = ColorRGBA.White;
        ColorRGBA endColour = ColorRGBA.Yellow;
        
        sparkles = new ParticleEmitter("Sparkles", ParticleMesh.Type.Triangle, 2);
        sparkles.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.5f));
        sparkles.setLocalTranslation(object.getLocalTranslation().add(0, 0, 0.2f));
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
    protected GameObject getGObjectClone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Objective(AssetManager assetManager) {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        loadParticles();
        defineObjectBounds();

        //Temp
        objectDimensions = new Vector3f(0.5f, 0.5f, 0.5f);

        gameObjectNode.attachChild(object);
        gameObjectNode.attachChild(goldGeo);
    }

    @Override
    public void update(float tpf) {
        sparkles.emitParticles((int) sparkles.getParticlesPerSec());
    }
}
