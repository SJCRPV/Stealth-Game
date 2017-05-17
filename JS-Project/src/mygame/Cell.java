/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJCRPV
 */
public class Cell {
    
    private final int gridXCoor;
    private final int gridYCoor;
    List<Spatial> objectsInSubCells;
    
    public int[] getCellCoors()
    {
        return new int[] {gridXCoor, gridYCoor};
    }
    
    public Object getObjectInSubCell(int index)
    {
        return objectsInSubCells.get(index);
    }
    
    public void addObjectToSubCell(int index, Spatial objectToAdd)
    {
        if(index < 4 && objectsInSubCells.get(index) == null)
        {
            objectsInSubCells.add(index, objectToAdd);
        }
    }
    
    public boolean doesObjectExistInSubCell(int index)
    {
        if(index >= objectsInSubCells.size())
        {
            return false;
        }
        return objectsInSubCells.get(index) == null;
    }
    
    public Cell(int cellX, int cellY)
    {
        gridXCoor = cellX;
        gridYCoor = cellY;
        objectsInSubCells = new ArrayList<>(4);
    }
}
