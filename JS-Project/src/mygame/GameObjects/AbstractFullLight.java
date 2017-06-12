/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.GameObjects;

import com.jme3.app.state.AbstractAppState;

/**
 *
 * @author SJCRPV
 */
public abstract class AbstractFullLight extends AbstractAppState {
    //I know. The way it is now doesn't warrant the existence of this class. I just want to see if a need for more development
    //on this becomes necessary.
    static final int SHADOWMAP_SIZE = 1024;
    
    @Override
    public abstract void update(float tpf);
}
