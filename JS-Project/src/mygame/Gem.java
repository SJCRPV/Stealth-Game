/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author José Castanheira
 */
public final class Gem extends GameObject {

    
    private Material redmat;
    private AssetManager assetManager;
    private Geometry geom;
    
    public Geometry getGeom()
    {
        return geom;
    }
    
    public Gem(AssetManager assetManager, Vector3f location)
    {
        this.assetManager = assetManager;
        createMaterial();
        loadModel();
        place(location);
    }
    @Override
    protected void createMaterial() {
         redmat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md"); 
         redmat.setColor("Color",ColorRGBA.Red);
    }

    @Override
    protected void loadModel() {
       Box mesh = new Box(0.2f,0.2f,0.2f);
       geom = new Geometry("Gem",mesh);
       geom.setMaterial(redmat);
       geom.rotateUpTo(new Vector3f(0.5f,0.5f,0.5f));
    }
    
    protected void place(Vector3f location)
    {
        geom.setLocalTranslation(location);
    }
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInitialized() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEnabled(boolean active) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEnabled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(float tpf) {
        geom.rotate(0, 2*tpf, 0);
    }

    @Override
    public void render(RenderManager rm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void postRender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
