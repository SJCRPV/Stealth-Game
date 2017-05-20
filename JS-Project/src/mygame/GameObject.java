/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;

/**
 *
 * @author SJCRPV
 */
public abstract class GameObject implements AppState {
    
    protected abstract void createMaterial();
    protected abstract void loadModel();
    
}
