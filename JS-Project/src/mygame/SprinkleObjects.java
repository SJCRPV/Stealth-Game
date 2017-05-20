/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJCRPV
 */
public class SprinkleObjects extends Generation {
    
    Node sprinkledObjects;
    
    int playerSpawnRoomNum;
    
    
    private void sprinklePlayer()
    {
        playerSpawnRoomNum = generateRandomNum(0, completedAreas.size() - 1);
        int[] dimensions = completedAreas.get(playerSpawnRoomNum);
        BoundingBox spatHeight = (BoundingBox)player.getWorldBound();
        Vector3f location = assemblePos(dimensions[0], dimensions[2], 0, spatHeight.getZExtent());
        putObjectInPlace(player.clone(), location);
    }
    
    public Node sprinkle()
    {
        sprinklePlayer();
        sprinkleObjective();
        
        for(int i = 0; i < completedAreas.size(); i++)
        {
            
        }
        
        sprinkleTreasure();
        sprinkleObjects();
        sprinkleEnemies();
        
        return sprinkledObjects;
    }
}
