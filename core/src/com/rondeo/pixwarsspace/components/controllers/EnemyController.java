package com.rondeo.pixwarsspace.components.controllers;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.components.entity.EnemyShip;

public class EnemyController extends Actor implements Entity, Disposable {
    World<Entity> world;
    Array<EnemyShip> activeEnemies = new Array<>();
    Array<AtlasRegion> explosionRegions;
    private final Pool<EnemyShip> enemyPool = new Pool<EnemyShip>() {
        @Override
        protected EnemyShip newObject() {
            return new EnemyShip( world, explosionRegions );

        }
    };

    Random random = new Random();
    float[][] patterns_1 = {
        {
            .8f, 1.3f,
            .2f, .4f,
            .5f, .4f,
            .8f, .4f,
            .8f, .6f,
            .5f, .6f,
            .2f, .6f,
            .8f, .8f,
            .8f, 1.3f
        },
        {
            .8f, 1.3f,
            .8f, .4f,
            .5f, .4f,
            .2f, .4f,
            .2f, .8f,
            .5f, .8f,
            .8f, .8f,
            .8f, .6f,
            .5f, .6f,
            .2f, .6f,
            .2f, 1.3f
        },
        {
            -1.2f, .3f,
            .9f, .3f,
            .7f, .3f,
            .7f, .5f,
            .9f, .5f,
            .9f, 0.7f,
            .7f, .7f
        }
    };
    float[][] patterns_2 = {
        {
            .2f, 1.3f,
            .8f, .5f,
            .5f, .5f,
            .2f, .5f,
            .2f, .7f,
            .5f, .7f,
            .8f, .7f,
            .2f, .9f,
            .2f, 1.3f
        },
        {
            .2f, 1.3f,
            .2f, .5f,
            .5f, .5f,
            .8f, .5f,
            .8f, .9f,
            .5f, .9f,
            .2f, .9f,
            .2f, .7f,
            .5f, .7f,
            .8f, .7f,
            .8f, 1.3f
        },
        {
            1.2f, .3f,
            .1f, .3f,
            .3f, .3f,
            .3f, .5f,
            .1f, .5f,
            .1f, .7f,
            .3f, .7f
        }
    };
    

    AtlasRegion[] enemyRegion;
    int regionLength = 0;
    int choosenPatternIndex = 0;
    int choosenRegionIndex = 0;

    public EnemyController( World<Entity> world, Array<AtlasRegion> explosionRegions, int regionLength, AtlasRegion... enemyRegion ) {
        this.world = world;
        this.explosionRegions = explosionRegions;
        this.enemyRegion = enemyRegion;
        this.regionLength = regionLength;
    }

    EnemyShip enemyShip;
    public void deploy( Stage stage ) {
        // Deploy first
        enemyShip = enemyPool.obtain();
        stage.addActor( enemyShip );
        //System.out.println( patterns.get( choosenPatternIndex ) );
        enemyShip.init( enemyRegion[choosenRegionIndex], patterns_1[ choosenPatternIndex ] );
        activeEnemies.add( enemyShip );

        // Deploy second
        enemyShip = enemyPool.obtain();
        stage.addActor( enemyShip );
        enemyShip.init( enemyRegion[choosenRegionIndex], patterns_2[ choosenPatternIndex ] );
        activeEnemies.add( enemyShip );
    }
    
    public Action deployAction = new Action() {
        @Override
        public boolean act(float delta) {
            deploy( getStage() );
            return true;
        }
    };
    SequenceAction deploySequence = new SequenceAction();
    public SequenceAction deployShips() {
        deploySequence = new SequenceAction();
        choosenRegionIndex = random.nextInt( regionLength );
        choosenPatternIndex = random.nextInt( patterns_1.length );
        for( int i = 0; i < 12; i ++ ) {
            deploySequence.addAction( Actions.delay( .3f ) );
            deploySequence.addAction( deployAction );
        }
        return deploySequence;
    }

    long time;
    @Override
    public void act( float delta ) {
        super.act(delta);

        if( activeEnemies.size < 8 ) {
            if( System.currentTimeMillis() > time + 10000 ) {
                addAction( deployShips() );
                time = System.currentTimeMillis();
            }
        }
    }

    public void forceFree( EnemyShip item ) {
        enemyPool.free( item );
        item.dispose();
        activeEnemies.removeValue( item, false );
    }

    @Override
    public void dispose() {
        for ( EnemyShip enemyShip : activeEnemies ) {
            enemyShip.dispose();
        }
    }
    
}
