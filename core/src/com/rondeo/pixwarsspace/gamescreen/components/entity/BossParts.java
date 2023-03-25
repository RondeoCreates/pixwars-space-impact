package com.rondeo.pixwarsspace.gamescreen.components.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.utils.Rumble;
import com.rondeo.pixwarsspace.utils.SoundController;

public class BossParts implements Entity {
    public boolean isDead = false;
    public long isHit = 0;
    public int life = 60;
    //private World<Entity> world;
    private Item<Entity> item;

    public float x, y, width, height;

    private Animation<AtlasRegion> explosionAnimation;

    public BossParts( World<Entity> world, float x, float y, float width, float height, Array<AtlasRegion> explosionRegions ) {
        //this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        explosionAnimation = new Animation<>( .15f, explosionRegions );
        item = new Item<Entity>( this );
        world.add( item, x, y, width, height );
    }

    public void reduceLife() {
        life --;
        //System.out.println( life );
        if( life <= 0 )
            deltaTime = 0;
    }

    public void attack( Batch batch ) { }

    public void warning( Batch batch, AtlasRegion atlasRegion ) { }

    Color color = new Color();
    private float deltaTime;
    public void draw( Actor actor, Batch batch, AtlasRegion region, float delta ) {
        this.deltaTime += delta;
        if( isDead )
            return;
        if( !isDead ) {
            batch.draw( region, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight() );
            if( (isHit > System.currentTimeMillis() && life > 0) || life <= 0 ) {
                color.set( batch.getColor() );
                batch.setColor( 1, 0, 0, 1f );
                batch.draw( region, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight() );
                batch.setColor( color );
            } 
        }

        if( life <= 0 && !isDead ) {
            for( int i = 0; i < 3; i ++ ) {
                for( int j = 0; j < 3; j ++ ) {
                    if( !explosionAnimation.isAnimationFinished( Math.max( deltaTime - (i + j)/2, 0 ) ) ) {
                        if( !SoundController.getInstance().explosion.isPlaying() )
                            SoundController.getInstance().explosion.play();
                        Rumble.rumble( 4f, .4f );
                        batch.draw( explosionAnimation.getKeyFrame( Math.max( deltaTime - (i + j)/2, 0 ) ), ( x + (width/3)*i ) - 8, ( y + (height/3)*j ) - 8 );
                    }
                }
            }
            if( explosionAnimation.isAnimationFinished( Math.max( deltaTime - 2, 0 ) ) && !isDead ) {
                isDead = true;
            }
        }
    }
}