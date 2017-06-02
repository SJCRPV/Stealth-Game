/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.MapGeneration;

import mygame.GameObjects.Objective;
import mygame.GameObjects.Gem;
import mygame.GameObjects.Crate;
import mygame.GameObjects.Torch;
import mygame.GameObjects.GameObject;
import mygame.GameObjects.Player;
import mygame.GameObjects.Enemy;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJCRPV
 */
public class SprinkleObjects extends Generation {
    
    Node sprinkledObjects;
    Node mazeNode;
    AssetManager assetManager;
    List<GameObject> listOfGObjects;
    
    Player player;
    Camera cam;
    
    private final int TREASURE_VALUE;
    private final int MIN_CELL_DISTANCE_TO_PLAYER;
    private final int MAX_GAMEOBJECT_SPAWN_ATTEMPTS;
    private final int MAX_ENEMIES_PER_ROOM;
    private final int MAX_GEMS_PER_ROOM;
    private final int MAX_TORCHES_PER_ROOM;
    private final float GEM_CHANCE;
    private final float CRATE_CHANCE;
    private final float ENEMY_CHANCE;

    private int maxPointsInLevel;
    private int playerSpawnRoomNum;
    private int currentRoomNum;
    private int numOfEnemies;
    private int numOfGems;
    private Geometry playerGeo;
	
    public List<GameObject> getGOList()
    {
        //System.out.println("We have " + listOfGObjects.size() + " in our GO list.");
        return listOfGObjects;
    }
    
    private void putObjectInPlace(GameObject object, Vector3f location)
    {
        object.setLocalTranslation(location);
        listOfGObjects.add(object);
        sprinkledObjects.attachChild(object.getNode());
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

        grid[xCoor][yCoor].addObjectToSubCell(subCellNum, gameO);
        gameO.setCellCoordinates(xCoor, yCoor);

        return assemblePos(xCoor, yCoor, subCellNum, gameOHeight);
    }
    
    private void findLocation(GameObject object)
    {
        Vector3f location = whereToSprinkle(object);
        putObjectInPlace(object, location);
    }
    
    private void sprinkleCrate()
    {
        GameObject crate = new Crate(assetManager);
        findLocation(crate);
    }
    
    private void sprinkleTorch()
    {
        GameObject torch = new Torch(assetManager);
        findLocation(torch);
    }

    private void sprinkleGem()
    {
        GameObject gem = new Gem(assetManager);
        findLocation(gem);
        numOfGems++;
    }

    private void sprinkleEnemy()
    {
        GameObject enemy = new Enemy(assetManager,mazeNode);
        findLocation(enemy);
    }

    private void sprinkleObjective()
    {
        int objectiveSpawnRoomNum;
        GameObject objective = new Objective(assetManager);
        Vector3f location;
        int[] playCellCoor = player.getCellCoordinates();
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
        Box p = new Box(0.25f,0.25f,0.25f);
        Geometry pg = new Geometry("Player",p);
        Material pm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pm.setColor("Color", ColorRGBA.White);
        pg.setMaterial(pm);
        pg.setLocalTranslation(location);
        sprinkledObjects.attachChild(pg);
        playerGeo = pg;
    }
	
    public Geometry getPlayer()
    {
        return playerGeo;
    }
    
    public Node sprinkle()
    {
        sprinklePlayer();
        sprinkleObjective();
        
        for(currentRoomNum = 0; currentRoomNum < completedAreas.size(); currentRoomNum++)
        {
            int currentChance = generateRandomNum(1, 100);
            for(int i = 0, gemCounter = 0, enemyCounter = 0, torchCounter = 0; i < MAX_GAMEOBJECT_SPAWN_ATTEMPTS; i++)
            {
                if(currentChance < CRATE_CHANCE)
                {
                    sprinkleCrate();
                }
                if(torchCounter < MAX_TORCHES_PER_ROOM)
                {
                    sprinkleTorch();
                    torchCounter++;
                    i++;
                }
                if(currentChance < GEM_CHANCE && gemCounter < MAX_GEMS_PER_ROOM)
                {
                    sprinkleGem();
                    gemCounter++;
                    i++;
                }
                if(currentChance < ENEMY_CHANCE && currentRoomNum != playerSpawnRoomNum && enemyCounter < MAX_ENEMIES_PER_ROOM)
                {
                    sprinkleEnemy();
                    enemyCounter++;
                    i++;
                }
            }
        }
        maxPointsInLevel = numOfGems * TREASURE_VALUE;
        return sprinkledObjects;
    }
    
    public SprinkleObjects(AssetManager newAssetManager, Camera cam, Node mazeNode, int treasurePointValue, int minDistanceToPlayer, 
            int maxEnemiesPerRoom, int maxTorchesPerRoom, int maxGemsPerRoom, float enemyChance, float crateChance, float treasureChance)
    {
        this.cam = cam;
        this.mazeNode = mazeNode;
        sprinkledObjects = new Node();
        assetManager = newAssetManager;
        TREASURE_VALUE = treasurePointValue;
        MIN_CELL_DISTANCE_TO_PLAYER = minDistanceToPlayer;
        MAX_ENEMIES_PER_ROOM = maxEnemiesPerRoom;
        MAX_TORCHES_PER_ROOM = maxTorchesPerRoom;
        MAX_GEMS_PER_ROOM = maxGemsPerRoom;
        MAX_GAMEOBJECT_SPAWN_ATTEMPTS = MAX_ENEMIES_PER_ROOM + MAX_TORCHES_PER_ROOM + MAX_GEMS_PER_ROOM;        
        ENEMY_CHANCE = enemyChance;
        CRATE_CHANCE = crateChance;
        GEM_CHANCE = treasureChance;
        numOfEnemies = Math.round(completedAreas.size() * 0.9f);

        listOfGObjects = new ArrayList();
    }
}
