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
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author SJCRPV
 */
public class SprinkleObjects extends Generation {
    
    Node sprinkledObjects;
    AssetManager assetManager;
    List<GameObject> listOfGObjects;
    
    GameObject player;
    Camera cam;
    
    FlowerPot flowerPot;
    Desk desk;
    
    private final Vector3f rootNodeWC;
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
    private int numOfTreasures;
	
    public List<GameObject> getGOList()
    {
        System.out.println("We have " + listOfGObjects.size() + " in our GO list.");
        return listOfGObjects;
    }
    
    private void putObjectInPlace(GameObject object, Vector3f location)
    {
        object.setLocation(location);
        listOfGObjects.add(object);
        sprinkledObjects.attachChild(object.getSpatial());
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
        GameObject enemy = new Enemy(assetManager);
        Vector3f location = whereToSprinkle(enemy);
        putObjectInPlace(enemy, location);
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

    private Vector3f transformToWorldCoor(Vector3f localVector)
    {
        return rootNodeWC.add(sprinkledObjects.getWorldTranslation().add(localVector));
    }
    
    private void sprinklePlayer()
    {
        playerSpawnRoomNum = generateRandomNum(0, completedAreas.size() - 1);
//        player = new Player(assetManager);
//        Vector3f location = whereToSprinkle(player);
//        location = transformToWorldCoor(location);
//        putObjectInPlace(player, location);
//        player.placeObject(location);
//        player.setFollowingCameraNode(cam);
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
        //sprinklePlayer();
        sprinkleObjective();
        
        for(currentRoomNum = 0; currentRoomNum < completedAreas.size(); currentRoomNum++)
        {
            //int gObjectsInRoom = generateRandomNum(0, MAX_GAMEOBJECTS_PER_ROOM);
            int currentChance = generateRandomNum(1, 100);
            for(int i = 0; i < MAX_GAMEOBJECTS_PER_ROOM; i++)
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
                if(currentChance < TREASURE_CHANCE && numOfTreasures > 0)
                {
                    sprinkleTreasure();
                    i++;
                    numOfTreasures--;
                }
                if(currentChance < ENEMY_CHANCE && numOfEnemies > 0 && currentRoomNum != playerSpawnRoomNum)
                {
                    sprinkleEnemy();
                    i++;
                    numOfEnemies--;
                }
            }
        }
        return sprinkledObjects;
    }
    
    public SprinkleObjects(AssetManager newAssetManager, Camera cam, Vector3f rootWC, int treasurePointValue, int maxPointsInArea,
            int minDistanceToPlayer, int maxObjectsPerRoom, float enemyChance, float objectChance, float treasureChance)
    {
        sprinkledObjects = new Node();
        this.cam = cam;
        rootNodeWC = rootWC;
        assetManager = newAssetManager;
        TREASURE_VALUE = treasurePointValue;
        MAX_POINTS_IN_LEVEL = maxPointsInArea;
        MIN_CELL_DISTANCE_TO_PLAYER = minDistanceToPlayer;
        MAX_GAMEOBJECTS_PER_ROOM = maxObjectsPerRoom;
        ENEMY_CHANCE = enemyChance;
        OBJECT_CHANCE = objectChance;
        TREASURE_CHANCE = treasureChance;
        numOfEnemies = Math.round(completedAreas.size() * 0.9f);

        numOfTreasures = Math.round(MAX_POINTS_IN_LEVEL/TREASURE_VALUE);
        listOfGObjects = new ArrayList();
        
        
        flowerPot = new FlowerPot(assetManager);
        StandardObject.addToObjectList(flowerPot);
        desk = new Desk(assetManager);
        StandardObject.addToObjectList(desk);
    }
}
