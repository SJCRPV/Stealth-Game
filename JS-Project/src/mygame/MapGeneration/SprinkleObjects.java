/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.MapGeneration;

import mygame.GameObjects.Objective;
import mygame.GameObjects.Gem;
import mygame.GameObjects.Desk;
import mygame.GameObjects.FlowerPot;
import mygame.GameObjects.GameObject;
import mygame.GameObjects.Player;
import mygame.GameObjects.Enemy;
import mygame.GameObjects.StandardObject;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.lang.reflect.InvocationTargetException;


/**
 *
 * @author SJCRPV
 */
public class SprinkleObjects extends Generation {
    
    Node sprinkledObjects;
    AssetManager assetManager;
    GameObject player;
    
    FlowerPot flowerPot;
    Desk desk;
    
    private final int TREASURE_VALUE;
    private final int MAX_POINTS_IN_LEVEL;
    private final int MIN_CELL_DISTANCE_TO_PLAYER;
    private final int MAX_GAMEOBJECTS_PER_ROOM;
    private final float TREASURE_CHANCE;
    private final float OBJECT_CHANCE;
    private final float ENEMY_CHANCE;
	
    private int playerSpawnRoomNum;
    private int currentRoomNum;
    private int numOfEnemies;
	
    private void putObjectInPlace(GameObject object, Vector3f location)
    {
        object.setLocalTranslation(location);
        sprinkledObjects.attachChild(object.getSpatialClone());
    }
	
    private boolean isItFarEnough(int[] obCellCoor, int[] playCellCoor)
    {
        Vector2f obVector = new Vector2f(obCellCoor[0], obCellCoor[1]);
        Vector2f playerVector = new Vector2f(playCellCoor[0], playCellCoor[1]);

        float totalCellDistance = Math.abs(obVector.x - playerVector.x) + Math.abs(obVector.y - playerVector.y);

        return totalCellDistance > MIN_CELL_DISTANCE_TO_PLAYER;
    }
    
    private boolean isPositionValid(int xCoor, int yCoor, int subCellNum)
    {
        Cell tempCell = grid[xCoor][yCoor];
        return !(tempCell.isThereADoor() || tempCell.doesObjectExistInSubCell(subCellNum));
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
	
    private Vector3f whereToSprinkle(GameObject gameO)
    {
        boolean isValid;
        int xCoor = -1;
        int yCoor = -1;
        int subCellNum = -1;
        float gameOHeight = gameO.getObjectDimensions().z;

        do
        {
            int[] dimensions = completedAreas.get(currentRoomNum);
            xCoor = generateRandomNum(dimensions[0], dimensions[1]);
            yCoor = generateRandomNum(dimensions[2], dimensions[3]);
            subCellNum = generateRandomNum(0, 3);
            isValid = isPositionValid(xCoor, yCoor, subCellNum);
        } while(!isValid);

        //TODO: Account for objects that occupy multiple subCells. 
        //TODO: Account for vertical or horizontal alignment.
        grid[xCoor][yCoor].addObjectToSubCell(subCellNum, gameO);
        gameO.setCellCoordinates(xCoor, yCoor);

        return assemblePos(xCoor, yCoor, subCellNum, gameOHeight);
    }
	
    private void sprinkleObject() throws InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
    {
        int objectNum = generateRandomNum(0, StandardObject.getObjectList().size());        
        StandardObject newObject = StandardObject.getObjectAt(objectNum).getClass().getConstructor(AssetManager.class).newInstance(assetManager);      
        Vector3f location = whereToSprinkle(newObject);
        putObjectInPlace(newObject, location);
    }

    private void sprinkleTreasure()
    {
        GameObject treasure = new Gem(assetManager);
        Vector3f location = whereToSprinkle(treasure);
        putObjectInPlace(treasure, location);
    }

    private void sprinkleEnemy()
    {
        if(numOfEnemies > 0 && currentRoomNum != playerSpawnRoomNum)
        {
            GameObject enemy = new Enemy(assetManager);
            Vector3f location = whereToSprinkle(enemy);
            putObjectInPlace(enemy, location);
            numOfEnemies--;
        }
    }

    private void sprinkleObjective()
    {
        int objectiveSpawnRoomNum;
        GameObject objective = new Objective(assetManager);
        Vector3f location;
        //int[] playCellCoor = player.getCellCoordinates();
        int[] playCellCoor = new int[] {1, 1};
        int[] objCellCoor;
        do
        {
            objectiveSpawnRoomNum = generateRandomNum(0, completedAreas.size() - 1);
            location = whereToSprinkle(objective);
            objCellCoor = objective.getCellCoordinates();
        } while(objectiveSpawnRoomNum == playerSpawnRoomNum && !isItFarEnough(objCellCoor, playCellCoor));
        putObjectInPlace(objective, location);
    }

    private void sprinklePlayer()
    {
        playerSpawnRoomNum = generateRandomNum(0, completedAreas.size() - 1);
        player = new Player(assetManager);
        Vector3f location = whereToSprinkle(player);
        putObjectInPlace(player, location);
    }
	
    private void tryToSprinkleObject()
    {
        try
        {
            sprinkleObject();
        }
        catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            System.out.println(e);
        }
    }
    
    public Node sprinkle()
    {
        sprinklePlayer();
        sprinkleObjective();
        
        for(currentRoomNum = 0; currentRoomNum < completedAreas.size(); currentRoomNum++)
        {
            int gObjectsInRoom = generateRandomNum(0, MAX_GAMEOBJECTS_PER_ROOM);
            int currentChance = generateRandomNum(1, 100);
            for(int i = 0; i < gObjectsInRoom; i++)
            {
               if(currentChance < OBJECT_CHANCE)
               {
                   tryToSprinkleObject();
                   i++;
               }
               if(currentChance < TREASURE_CHANCE)
               {
                   sprinkleTreasure();
                   i++;
               }
               if(currentChance < ENEMY_CHANCE)
               {
                   sprinkleEnemy();
                   i++;
               }
            }
        }
        
        return sprinkledObjects;
    }
    
    public SprinkleObjects(AssetManager newAssetManager, int treasurePointValue, int maxPointsInArea, int minDistanceToPlayer, 
            int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
    {
        sprinkledObjects = new Node();
        assetManager = newAssetManager;
        TREASURE_VALUE = treasurePointValue;
        MAX_POINTS_IN_LEVEL = maxPointsInArea;
        MIN_CELL_DISTANCE_TO_PLAYER = minDistanceToPlayer;
        MAX_GAMEOBJECTS_PER_ROOM = maxObjectsPerRoom;
        ENEMY_CHANCE = enemyChance;
        OBJECT_CHANCE = objectChance;
        TREASURE_CHANCE = treasureChance;
        numOfEnemies = Math.round(completedAreas.size() * 0.9f);
        System.out.println("NumOfEnemies " + numOfEnemies);
        
        flowerPot = new FlowerPot(assetManager);
        StandardObject.addToObjectList(flowerPot);
        desk = new Desk(assetManager);
        StandardObject.addToObjectList(desk);
    }
}
