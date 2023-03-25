package com.rondeo.pixwarsspace.gamescreen.components.controllers;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.gamescreen.components.entity.PowerUp;

public class PowerUpController implements Disposable {
    World<Entity> world;
    AtlasRegion[] powerRegion;

    public PowerUpController( World<Entity> world, AtlasRegion... powerRegion ) {
        this.world = world;
        this.powerRegion = powerRegion;
        //for ( AtlasRegion atlasRegion : powerRegion) {
        //    System.out.println( atlasRegion );
        //}
    }

    Array<PowerUp> activePowerUps = new Array<>();
    private final Pool<PowerUp> powerPool = new Pool<PowerUp>() {
        @Override
        protected PowerUp newObject() {
            return new PowerUp( world );
        }
    };

    Random random = new Random();
    PowerUp powerUp;
    public void pop( Stage stage, float x, float y ) {
        if( random.nextBoolean() ) {
            if( random.nextBoolean() ) {
                if( random.nextBoolean() ) {
                    int index = random.nextInt( powerRegion.length );
                    if( index > 2 && !random.nextBoolean() ) {
                        index = 0;
                    }
                    //System.out.println( x + "," + y );
                    powerUp = powerPool.obtain();
                    stage.addActor( powerUp );
                    powerUp.init( powerRegion[index], index, x, y );
                    //activePowerUps.add( powerUp );
                }
            }
        }
    }

    public void forceFree( PowerUp item ) {
        powerPool.free( item );
        item.dispose();
        //activePowerUps.removeValue( item, false );
    }

    @Override
    public void dispose() {
        for ( PowerUp powerUp : activePowerUps ) {
            powerUp.dispose();
        }
    }
    
}
