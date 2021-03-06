/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.MapGeneration;

import mygame.GameObjects.GameObject;
import com.jme3.scene.Spatial;

/**
 *
 * @author SJCRPV
 */
public class Cell {
    
    private final int gridXCoor;
    private final int gridYCoor;
    private boolean hasDoorNearby;
    GameObject[] objectsInSubCells;
    
    public boolean isThereADoor()
    {
        return hasDoorNearby;
    }
    
    public void doorExistsNow()
    {
        hasDoorNearby = true;
    }
    
    public int[] getCellCoors()
    {
        return new int[] {gridXCoor, gridYCoor};
    }
    
    public Object getObjectInSubCell(int index)
    {
        return objectsInSubCells[index];
    }
    
    public void addObjectToSubCell(int index, GameObject objectToAdd)
    {
        if(index < 4 && objectsInSubCells[index] == null)
        {
            objectsInSubCells[index] = objectToAdd;
        }
    }
    
    public boolean doesObjectExistInSubCell(int index)
    {
        return objectsInSubCells[index] != null;
    }
    
    public Cell(int cellX, int cellY)
    {
        gridXCoor = cellX;
        gridYCoor = cellY;
        objectsInSubCells = new GameObject[4];
        hasDoorNearby = false;
    }
}
