
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
/*
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
*/
/**
 *
 * @author SJCRPV
 */
public class oldSprinkleObjects extends Generation {
    
    /*Node sprinkledObjects;
    AssetManager assetManager;
    Spatial player;
    Spatial objective;
    Spatial flowerPot;
    Spatial computerDesk;
    Spatial treasure;
    Spatial enemy;
    List<Spatial> objectList;
    Material playerMat;
    Material objectiveMat;
    Material flowerPotMat;
    Material computerDeskMat;
    Material treasureMat;
    Material enemyMat;
    int playerSpawnRoomNum;
    final int TREASURE_VALUE;
    final int MAX_POINTS_IN_LEVEL;
    final int NUM_OBJECTS_IN_LEVEL;
    final float MIN_EUCLIDEAN_DISTANCE_TO_PLAYER;
    final float ENEMY_TO_ROOM_RATIO;
    
    private void putObjectInPlace(Spatial object, Vector3f location)
    {
        object.setLocalTranslation(location);
        sprinkledObjects.attachChild(object);
    }
    
    private boolean isPositionValid(int xCoor, int yCoor, int subCellNum)
    {
        Cell tempCell = grid[xCoor][yCoor];
        return !(tempCell.isThereADoor() || tempCell.doesObjectExistInSubCell(subCellNum));
    }
    
    private Spatial getSpatialToSprinkle()
    {
        return objectList.get(generateRandomNum(0, objectList.size()));
    }
    
    private Vector3f assemblePos(int xCoor, int yCoor, int subCellNum, float objectHeight)
    {
        float xPos = calcSize(xCoor);
        float yPos = calcSize(yCoor);
        
        switch(subCellNum)
        {
            case 0:
                return new Vector3f(xPos, yPos, objectHeight);
            
            case 1:
                return new Vector3f(xPos + CELL_WIDTH/2f, yPos, objectHeight);
                
            case 2:
                return new Vector3f(xPos, yPos + CELL_HEIGHT/2f, objectHeight);
                
            case 3:
                return new Vector3f(xPos + CELL_WIDTH/2f, yPos + CELL_HEIGHT/2f, objectHeight);
                
            default:
                System.out.println("Tried to access a subcell that does not exist");
                return new Vector3f(-1,-1,-1);
        }
    }
    
    private Vector3f whereToSprinkle(Spatial spat)
    {
        boolean isValid = false;
        int xCoor = -1;
        int yCoor = -1;
        int subCellNum = -1;
        int roomNum;
        BoundingBox spatHeight = (BoundingBox)spat.getWorldBound();
        
        do
        {
            roomNum = generateRandomNum(0, completedAreas.size());
            if(spat.equals(enemy))
            {
                if(roomNum == playerSpawnRoomNum)
                {
                    continue;
                }
            }

            int[] dimensions = completedAreas.get(roomNum);
            xCoor = generateRandomNum(dimensions[0], dimensions[1]);
            yCoor = generateRandomNum(dimensions[2], dimensions[3]);
            subCellNum = generateRandomNum(0, 3);
            isValid = isPositionValid(xCoor, yCoor, subCellNum);
        } while(!isValid);
        
        grid[xCoor][yCoor].addObjectToSubCell(subCellNum, spat);
        return assemblePos(xCoor, yCoor, subCellNum, spatHeight.getZExtent());
    }
    
    private void sprinkleObjective()
    {
        Vector3f location;
        Vector3f playerLocation = player.getLocalTranslation();
        do
        {
            location = whereToSprinkle(objective);
        } while(playerLocation.distance(location) <= MIN_EUCLIDEAN_DISTANCE_TO_PLAYER);
        putObjectInPlace(objective.clone(), location);
    }
    
    private void sprinkleEnemies()
    {
        int numOfEnemies = Math.round(completedAreas.size() * ENEMY_TO_ROOM_RATIO);
        for(int i = 0; i < numOfEnemies; i++)
        {
            Vector3f location = whereToSprinkle(enemy);
            putObjectInPlace(enemy.clone(), location);
        }
    }
    
    private void sprinkleObjects()
    {
        for(int i = 0; i < NUM_OBJECTS_IN_LEVEL; i++)
        {
            Spatial spat = getSpatialToSprinkle();
            Vector3f location = whereToSprinkle(spat);
            putObjectInPlace(spat.clone(), location);
        }
    }
    
    private void sprinkleTreasure()
    {
        int numOfTreasures = MAX_POINTS_IN_LEVEL/TREASURE_VALUE;
        for(int i = 0; i < numOfTreasures; i++)
        {
            Vector3f location = whereToSprinkle(treasure);
            putObjectInPlace(treasure.clone(), location);
        }
    }
    
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
        sprinkleTreasure();
        sprinkleObjects();
        sprinkleEnemies();
        sprinkleObjective();
        
        return sprinkledObjects;
    }
    
    private void loadModels()
    {
        //TODO: Models to be loaded
        Box flowerPotBox = new Box(0.125f, 0.125f, 0.5f);
        flowerPot = new Geometry("Flower Pot", flowerPotBox);
        flowerPot.setMaterial(flowerPotMat);
        objectList.add(flowerPot);
        
        Box computerDeskBox = new Box(0.75f, 0.125f, 0.25f);
        computerDesk = new Geometry("Computer Desk", computerDeskBox);
        computerDesk.setMaterial(computerDeskMat);
        objectList.add(computerDesk);
        
        //Don't add anything past this point to the objectList. They're seperate.
        Box treasureBox = new Box(0.25f, 0.25f, 0.25f);
        treasure = new Geometry("Treasure", treasureBox);
        treasure.setMaterial(treasureMat);
        
        Box enemyBox = new Box(0.25f, 0.25f, 0.5f);
        enemy = new Geometry("Enemy", enemyBox);
        enemy.setMaterial(enemyMat);
        
        Box playerBox = enemyBox;
        player = new Geometry("Player", playerBox);
        player.setMaterial(playerMat);
        
        Box objectiveBox = treasureBox;
        objective = new Geometry("Objective", objectiveBox);
        objective.setMaterial(objectiveMat);
    }
    
    private void createMaterials()
    {
        flowerPotMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        flowerPotMat.setColor("Color", ColorRGBA.Magenta);
        
        computerDeskMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        computerDeskMat.setColor("Color", ColorRGBA.Orange);
        
        treasureMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        treasureMat.setColor("Color", ColorRGBA.Yellow);
        
        enemyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        enemyMat.setColor("Color", ColorRGBA.Brown);
        
        playerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        playerMat.setColor("Color", ColorRGBA.Cyan);
        
        objectiveMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        objectiveMat.setColor("Color", ColorRGBA.White);
    }
    
    public oldSprinkleObjects(AssetManager newAssetManager, int numOfObjectsToSprinkle, int treasurePointValue, int maxPointsInArea,
            int minCellsDistanceToPlayer, float enemyToRoomRatio)
    {
        sprinkledObjects = new Node();
        assetManager = newAssetManager;
        NUM_OBJECTS_IN_LEVEL = numOfObjectsToSprinkle;
        TREASURE_VALUE = treasurePointValue;
        MAX_POINTS_IN_LEVEL = maxPointsInArea;
        MIN_EUCLIDEAN_DISTANCE_TO_PLAYER = new Vector3f(Vector3f.ZERO).distance(new Vector3f(CELL_WIDTH * minCellsDistanceToPlayer,
                0, 0));
        ENEMY_TO_ROOM_RATIO = enemyToRoomRatio;
        objectList = new ArrayList<>();
        createMaterials();
        loadModels();
    }*/
}
