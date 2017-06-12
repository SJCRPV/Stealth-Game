/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 *
 * @author SJCRPV
 */
public class FullLight extends AbstractFullLight{
    
    AssetManager assetManager;
    PointLight light;
    PointLightShadowRenderer shadow;
    //PointLightShadowFilter shadow;
    ViewPort viewPort;
    Vector3f directionToPlayer;
    
    public void setRadius(float newRadius)
    {
        light.setRadius(newRadius);
    }
    
    public void removeShadow()
    {
        //viewPort.getProcessors().remove(shadow);
        viewPort.removeProcessor(shadow);
        shadow.cleanup();
    }
    
    public PointLight getLight()
    {
        return light;
    }
    
    public boolean castRay(Node node, Player player)
    {
        CollisionResults results = new CollisionResults();
        directionToPlayer = player.getWorldTranslation().subtract(light.getPosition());
        Ray ray = new Ray(light.getPosition(), directionToPlayer);
        node.collideWith(ray, results);
        
        if(results.size() > 0)
        {
            CollisionResult closest = results.getClosestCollision();
            return closest.getGeometry().getName().equalsIgnoreCase("Sinbad-geom-2");
        }
        return false;
    }
    
    private void setShadow()
    {
        shadow = new PointLightShadowRenderer(assetManager, SHADOWMAP_SIZE);
        shadow.setLight(light);
        viewPort.addProcessor(shadow);
        
//        shadow = new PointLightShadowFilter(assetManager, SHADOWMAP_SIZE);
//        shadow.setLight(light);
//        shadow.setEnabled(true);
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(shadow);
//        viewPort.addProcessor(fpp);
    }
    
    public void addShadow()
    {
        setShadow();
    }
    
    public FullLight(AssetManager assetManager, ViewPort viewPort, GameObject gObject, ColorRGBA colour, float lightRadius, 
            Vector3f position)
    {
        light = new PointLight();
        light.setColor(colour);
        light.setRadius(lightRadius);
        light.setPosition(gObject.getWorldTranslation().add(position));
        this.assetManager = assetManager;
        this.viewPort = viewPort;
        setShadow();
    }

    @Override
    public void update(float tpf) {
        
    }
}
