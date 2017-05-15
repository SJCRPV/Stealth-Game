/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author SJCRPV
 */
public class SprinkleObjects {
    
    AssetManager assetManager;
    Material flowerPotMat;
    Material computerDeskMat;
    
    private void createMaterials()
    {
        flowerPotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        flowerPotMat.setColor("Color", ColorRGBA.Magenta);
        computerDeskMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        computerDeskMat.setColor("Color", ColorRGBA.Orange);
    }
    
    public SprinkleObjects(AssetManager newAssetManager)
    {
        assetManager = newAssetManager;
        createMaterials();
    }
}
