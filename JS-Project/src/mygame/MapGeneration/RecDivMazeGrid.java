/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.MapGeneration;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJCRPV
 */
public class RecDivMazeGrid extends Generation {

    Node generatedMaze;
    AssetManager assetManager;
    Geometry plane;
    Geometry ceil;
    Material wallMat;
    Material floorMat;
    Material cellMat;
    Material ceilMat;
    List<int[]> minMaxWideList;
    List<int[]> minMaxTallList;
    final float Z_HEIGHT_OF_ALL = 2.8f;
    final int MIN_CELLS_WIDE;
    final int MIN_CELLS_TALL;
    boolean cutIsHorizontal;
    private RigidBodyControl rbFloor;
    protected BulletAppState bulletAppState;

    //Temp method to test wall placement.
    public Node getNode() {
        return generatedMaze;
    }

    public List<int[]> getCompletedAreas() {
        return completedAreas;
    }

    private boolean isCutHorizontal(int cellsWide, int cellsTall) {
        if (cellsTall == cellsWide) {
            return generateRandomNum(1, 2) == 1;
        }
        return cellsTall > cellsWide;
    }

    //TODO: Make doorExistsNow. You've only carved the place
    private float[] carveDoor(int startCoor, int numOfCells) {
        int doorCellNum = generateRandomNum(startCoor, startCoor + numOfCells);

        float doorPosition = calcSize(doorCellNum);
        float doorSize = calcSize(DOOR_SIZE);
        float leftDownWallSize = calcSize(doorCellNum - startCoor);
        float rightUpWallSize = calcSize(numOfCells - ((doorCellNum - startCoor) + DOOR_SIZE));

        float[] ret = {doorCellNum, doorPosition, doorSize, leftDownWallSize, rightUpWallSize};
        return ret;
    }

    private void markDoor(int x, int y) {
        Cell tempCell = grid[x][y];
        tempCell.doorExistsNow();
    }

    //TODO: Clean this function into smaller pieces
    private void createWall(int gridStartCoorX, int gridStartCoorY, int numOfCells, String geomName) {
        Geometry[] geoms = new Geometry[2];
        Box[] boxes = new Box[2];

        float fullWallStartXPos = calcSize(gridStartCoorX);
        float fullWallStartYPos = calcSize(gridStartCoorY);

        float[] carveValues;

        if (cutIsHorizontal) {
            carveValues = carveDoor(gridStartCoorX, numOfCells);
            markDoor((int) carveValues[0], gridStartCoorY);
            markDoor((int) carveValues[0], gridStartCoorY - 1);
        } else {
            carveValues = carveDoor(gridStartCoorY, numOfCells);
            markDoor(gridStartCoorX, (int) carveValues[0]);
            markDoor(gridStartCoorX - 1, (int) carveValues[0]);
        }

        float doorLocation = carveValues[1];

        float doorSize = carveValues[2];

        float leftDownWallSize = carveValues[3];
        float leftDownXPos;
        float leftDownYPos;

        float rightUpWallSize = carveValues[4];
        float rightUpXPos;
        float rightUpYPos;

        if (cutIsHorizontal) {
            boxes[0] = new Box(leftDownWallSize / 2f, WALL_THICKNESS / 2f, Z_HEIGHT_OF_ALL / 2f);
            leftDownXPos = doorLocation - doorSize / 2f - leftDownWallSize / 2f + WALL_THICKNESS * 1.5f;
            leftDownYPos = fullWallStartYPos - WALL_THICKNESS * 1.5f;
            geoms[0] = new Geometry("left" + geomName, boxes[0]);
            boxes[0].scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, leftDownWallSize / 2f));
            TangentBinormalGenerator.generate(boxes[0]);

            //Add physics
            RigidBodyControl rbwall1 = new RigidBodyControl(0.0f);
            geoms[0].addControl(rbwall1);
            rbwall1.setKinematic(true);
            bulletAppState.getPhysicsSpace().add(rbwall1);

            boxes[1] = new Box(rightUpWallSize / 2f, WALL_THICKNESS / 2f, Z_HEIGHT_OF_ALL / 2f);
            rightUpXPos = doorLocation + doorSize / 2f + rightUpWallSize / 2f + WALL_THICKNESS * 0.5f;
            rightUpYPos = fullWallStartYPos - WALL_THICKNESS * 1.5f;
            geoms[1] = new Geometry("right" + geomName, boxes[1]);
            boxes[1].scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, rightUpWallSize / 2f));
            TangentBinormalGenerator.generate(boxes[1]);

            //Add physics
            RigidBodyControl rbwall2 = new RigidBodyControl(0.0f);
            geoms[1].addControl(rbwall2);
            rbwall2.setKinematic(true);
            bulletAppState.getPhysicsSpace().add(rbwall2);

        } else {
            boxes[0] = new Box(WALL_THICKNESS / 2f, leftDownWallSize / 2f, Z_HEIGHT_OF_ALL / 2f);
            leftDownXPos = fullWallStartXPos - WALL_THICKNESS * 1.5f;
            leftDownYPos = doorLocation - doorSize / 2f - leftDownWallSize / 2f + WALL_THICKNESS * 1.5f;
            geoms[0] = new Geometry("down" + geomName, boxes[0]);
            boxes[0].scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, leftDownWallSize / 2f));
            TangentBinormalGenerator.generate(boxes[0]);

            //Add physics
            RigidBodyControl rbwall1 = new RigidBodyControl(0.0f);
            geoms[0].addControl(rbwall1);
            rbwall1.setKinematic(true);
            bulletAppState.getPhysicsSpace().add(rbwall1);

            boxes[1] = new Box(WALL_THICKNESS / 2f, rightUpWallSize / 2f, Z_HEIGHT_OF_ALL / 2f);
            rightUpXPos = fullWallStartXPos - WALL_THICKNESS * 1.5f;
            rightUpYPos = doorLocation + doorSize / 2f + rightUpWallSize / 2f + WALL_THICKNESS * 0.5f;
            geoms[1] = new Geometry("up" + geomName, boxes[1]);
            boxes[1].scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, rightUpWallSize / 2f));
            TangentBinormalGenerator.generate(boxes[1]);

            //Add physics
            RigidBodyControl rbwall2 = new RigidBodyControl(0.0f);
            geoms[1].addControl(rbwall2);
            rbwall2.setKinematic(true);
            bulletAppState.getPhysicsSpace().add(rbwall2);
        }

        geoms[0].setMaterial(wallMat);
        geoms[1].setMaterial(wallMat);

        geoms[0].setLocalTranslation(leftDownXPos, leftDownYPos, Z_HEIGHT_OF_ALL / 2f);
        geoms[1].setLocalTranslation(rightUpXPos, rightUpYPos, Z_HEIGHT_OF_ALL / 2f);

        generatedMaze.attachChild(geoms[0]);
        generatedMaze.attachChild(geoms[1]);
    }

    //TODO? Disallow random numbers to create rooms with width or height below minimums
    private void divideArea() {
        int randomCoor;
        int[] minMaxWide = minMaxWideList.remove(0);
        int[] minMaxTall = minMaxTallList.remove(0);
        int cellsWide = minMaxWide[1] - minMaxWide[0];
        int cellsTall = minMaxTall[1] - minMaxTall[0];
        cutIsHorizontal = isCutHorizontal(cellsWide, cellsTall);

        if (cellsWide <= MIN_CELLS_TALL || cellsTall <= MIN_CELLS_TALL) {
            completedAreas.add(new int[]{minMaxWide[0], minMaxWide[1], minMaxTall[0], minMaxTall[1]});
            return;
        }

        if (cutIsHorizontal) {
            randomCoor = generateRandomNum(minMaxTall[0] + 1, minMaxTall[1] - 1);
            createWall(minMaxWide[0], randomCoor, cellsWide, (minMaxWide[0] + ", " + randomCoor));

            minMaxWideList.add(minMaxWide);
            minMaxTallList.add(new int[]{minMaxTall[0], randomCoor});

            minMaxWideList.add(minMaxWide);
            minMaxTallList.add(new int[]{randomCoor, minMaxTall[1]});
        } else {
            randomCoor = generateRandomNum(minMaxWide[0] + 1, minMaxWide[1] - 1);
            createWall(randomCoor, minMaxTall[0], cellsTall, (randomCoor + ", " + minMaxTall[0]));

            minMaxWideList.add(new int[]{minMaxWide[0], randomCoor});
            minMaxTallList.add(minMaxTall);

            minMaxWideList.add(new int[]{randomCoor, minMaxWide[1]});
            minMaxTallList.add(minMaxTall);
        }
    }

    private void recursiveDivision() {
        while (minMaxWideList.size() > 0) {
            divideArea();
        }
    }

    public Node generateMaze() {
        recursiveDivision();
        return generatedMaze;
    }

    //TODO: Clean the box generation code. Here and in createWall. There are too many WALL_THICKNESS * 1.5f.
    //It's what's causing the necessity that cellSize and WALL_THICKNESS need to have a proportion of 2:1
    private void createBorders(int numOfCellsWide, int numOfCellsTall) {
        float maxWidth = calcSize(numOfCellsWide);
        float maxHeight = calcSize(numOfCellsTall);
        float s1 = (maxWidth / 2f - WALL_THICKNESS / 2f);
        float s2 = (WALL_THICKNESS / 2f);

        Box up = new Box(s1, s2, Z_HEIGHT_OF_ALL / 2f);
        Box left = new Box(s2, s1, Z_HEIGHT_OF_ALL / 2f);
        Box down = new Box(s1, s2, Z_HEIGHT_OF_ALL / 2f);
        Box right = new Box(s2, s1, Z_HEIGHT_OF_ALL / 2f);

        up.scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, s1));
        left.scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, s1));
        down.scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, s1));
        right.scaleTextureCoordinates(new Vector2f(Z_HEIGHT_OF_ALL, s1));

        /**
         * //TEST BOX Box bt = new Box(1,1,2); Geometry bgeom = new
         * Geometry("test",bt); bt.scaleTextureCoordinates(new Vector2f(2,2));
         * TangentBinormalGenerator.generate(bt); bgeom.setMaterial(floorMat);
         * bgeom.setLocalTranslation(new Vector3f(-2,-2,-2));
         *
         * generatedMaze.attachChild(bgeom); //TESTBOX
        *
         */
        Geometry upGeom = new Geometry("UpBorder", up);
        Geometry leftGeom = new Geometry("LeftBorder", left);
        Geometry downGeom = new Geometry("DownBorder", down);
        Geometry rightGeom = new Geometry("RightBorder", right);

        TangentBinormalGenerator.generate(up);
        TangentBinormalGenerator.generate(down);
        TangentBinormalGenerator.generate(left);
        TangentBinormalGenerator.generate(right);

        upGeom.setMaterial(wallMat);
        leftGeom.setMaterial(wallMat);
        downGeom.setMaterial(wallMat);
        rightGeom.setMaterial(wallMat);

        upGeom.setLocalTranslation(maxWidth / 2f - WALL_THICKNESS * 1.5f, maxHeight - WALL_THICKNESS * 1.5f, Z_HEIGHT_OF_ALL / 2f);
        leftGeom.setLocalTranslation(0 - WALL_THICKNESS * 1.5f, maxHeight / 2f - WALL_THICKNESS * 1.5f, Z_HEIGHT_OF_ALL / 2f);
        downGeom.setLocalTranslation(maxWidth / 2f - WALL_THICKNESS * 1.5f, 0 - WALL_THICKNESS * 1.5f, Z_HEIGHT_OF_ALL / 2f);
        rightGeom.setLocalTranslation(maxWidth - WALL_THICKNESS * 1.5f, maxHeight / 2f - WALL_THICKNESS * 1.5f, Z_HEIGHT_OF_ALL / 2f);

        //Add rigidbodies to physics space
        RigidBodyControl rbUWall = new RigidBodyControl(0.0f);
        upGeom.addControl(rbUWall);
        rbUWall.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(rbUWall);
        RigidBodyControl rbDWall = new RigidBodyControl(0.0f);
        downGeom.addControl(rbDWall);
        rbDWall.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(rbDWall);
        RigidBodyControl rbLWall = new RigidBodyControl(0.0f);
        leftGeom.addControl(rbLWall);
        rbLWall.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(rbLWall);
        RigidBodyControl rbRWall = new RigidBodyControl(0.0f);
        rightGeom.addControl(rbRWall);
        rbRWall.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(rbRWall);

        generatedMaze.attachChild(upGeom);
        generatedMaze.attachChild(leftGeom);
        generatedMaze.attachChild(downGeom);
        generatedMaze.attachChild(rightGeom);
    }

    private void representOnMaze(Vector3f position) {
        Box box = new Box(CELL_WIDTH, CELL_HEIGHT, 0f);
        Geometry geom = new Geometry("Cell", box);
        geom.setMaterial(cellMat);
        position.x += CELL_WIDTH / 2f;
        position.y += CELL_HEIGHT / 2f;
        geom.setLocalTranslation(position);

        generatedMaze.attachChild(geom);
    }

    private void createCell(int x, int y, Vector3f position) {
        //Constructor Cell(Vector3f position, int cellX, int cellY)
        grid[x][y] = new Cell(x, y);
        //Remove comment to see cells
        //representOnMaze(position);
    }

    private void createBaseMap() {
        for (int i = 0; i < grid.length; i++) {
            float vectorDotY = calcSize(i);
            for (int j = 0; j < grid[i].length; j++) {
                float vectorDotX = calcSize(j);
                Vector3f temp = new Vector3f(vectorDotX, vectorDotY, 0);
                createCell(i, j, temp);
            }
        }
    }

    private void createPlane(int numCellsWide, int numCellsTall) {
        float planeWidth = calcSize(numCellsWide);
        float planeHeight = calcSize(numCellsTall);
        Quad planeMesh = new Quad(planeWidth, planeHeight);
        planeMesh.scaleTextureCoordinates(new Vector2f(planeWidth, planeHeight));
        
        Box floorMesh = new Box(planeWidth/2,planeHeight/2,0.001f);
        floorMesh.scaleTextureCoordinates(new Vector2f(planeWidth/2, planeHeight/2));
        plane = new Geometry("Floor", floorMesh);
        plane.setMaterial(floorMat);
        plane.setLocalTranslation(-WALL_THICKNESS + planeWidth/2, -WALL_THICKNESS + planeHeight/2,0.001f);
        TangentBinormalGenerator.generate(floorMesh);
        generatedMaze.attachChild(plane);

        ceil = new Geometry("Ceil", planeMesh);
        ceil.setMaterial(ceilMat);
        ceil.setLocalTranslation(-WALL_THICKNESS, -WALL_THICKNESS, Z_HEIGHT_OF_ALL - 0.1f);
        ceil.rotate(0, (float) Math.PI, 0);
        ceil.setLocalTranslation(planeWidth - WALL_THICKNESS, -WALL_THICKNESS, Z_HEIGHT_OF_ALL - 0.1f);
        generatedMaze.attachChild(ceil);

    }

    private void createMaterials() {
        //wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //wallMat.setColor("Color", ColorRGBA.Blue);
        wallMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture wallText = assetManager.loadTexture("178.JPG");
        wallText.setWrap(Texture.WrapMode.Repeat);
        wallMat.setTexture("DiffuseMap", wallText);
        Texture wallNormal = assetManager.loadTexture("178_norm.JPG");
        wallNormal.setWrap(Texture.WrapMode.Repeat);
        wallMat.setTexture("NormalMap", wallNormal);
        wallMat.setBoolean("UseMaterialColors", true);
        wallMat.setColor("Diffuse", ColorRGBA.White);  // minimum material color
        wallMat.setColor("Specular", ColorRGBA.White); // for shininess
        wallMat.setFloat("Shininess", 128f); // [1,128] for shininess

        floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture floorText = assetManager.loadTexture("195.JPG");
        floorText.setWrap(Texture.WrapMode.Repeat);
        floorMat.setTexture("DiffuseMap", floorText);
        Texture floorNormal = assetManager.loadTexture("195_norm.JPG");
        floorNormal.setWrap(Texture.WrapMode.Repeat);
        floorMat.setTexture("NormalMap", floorNormal);
        floorMat.setBoolean("UseMaterialColors", true);
        floorMat.setColor("Diffuse", ColorRGBA.White);  // minimum material color

        ceilMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture ceilText = assetManager.loadTexture("168.JPG");
        ceilText.setWrap(Texture.WrapMode.Repeat);
        ceilMat.setTexture("DiffuseMap", ceilText);

        cellMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cellMat.setColor("Color", ColorRGBA.Green);
    }
    
    public Geometry getPlane()
    {
        return plane;
    }

    public RecDivMazeGrid(AssetManager newAssetManager, BulletAppState bulletAppState, int numCellsWide, int numCellsTall,
            float cellWidth, float cellHeight, float wallThickness, int doorCellSize, int minCellsWide, int minCellsTall) {
        this.bulletAppState = bulletAppState;
        generatedMaze = new Node();
        minMaxWideList = new ArrayList<>();
        minMaxTallList = new ArrayList<>();
        completedAreas = new ArrayList<>();
        assetManager = newAssetManager;
        grid = new Cell[numCellsWide][numCellsTall];
        CELL_WIDTH = cellWidth;
        CELL_HEIGHT = cellHeight;
        WALL_THICKNESS = wallThickness;
        DOOR_SIZE = doorCellSize;
        MIN_CELLS_WIDE = minCellsWide;
        MIN_CELLS_TALL = minCellsTall;
        createMaterials();
        createBorders(numCellsWide, numCellsTall);
        createBaseMap();
        createPlane(numCellsWide, numCellsTall);
        minMaxWideList.add(new int[]{0, grid.length});
        minMaxTallList.add(new int[]{0, grid[0].length});

        //Add rigidbodies to physics space
        rbFloor = new RigidBodyControl(0.0f);
        plane.addControl(rbFloor);
        rbFloor.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(rbFloor);
    }
}
