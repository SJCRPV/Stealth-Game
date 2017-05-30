/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJCRPV
 */
public abstract class StandardObject extends GameObject {
    
    @Override
    public abstract void loadPhysics();
    @Override
    public abstract boolean handleCollisions(GameObject gObject);
    
    private static List<StandardObject> objectList = new ArrayList<>();
    
    protected int NUM_SUBCELLS_IT_OCCUPIES;
    
    public static void addToObjectList(StandardObject obj)
    {
        objectList.add(obj);
    }
    
    public static List<StandardObject> getObjectList()
    {
        return objectList;
    }
    
    public static StandardObject getObjectAt(int index)
    {
        return objectList.get(index);
    }
}
