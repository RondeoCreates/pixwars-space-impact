package com.rondeo.pixwarsspace.components.entity;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.components.Controllers;
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.components.controllers.SoundController;
import com.rondeo.pixwarsspace.utils.Rumble;

public class EnemyShip extends Actor implements Entity, Disposable, Poolable {
    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    CollisionFilter collisionFilter = new CollisionFilter() {
        Bullet bullet;
        Ship ship;
        @SuppressWarnings( "rawtypes" )
        @Override
        public Response filter( Item item, Item other ) {
            if( other.userData instanceof Bullet && !((Bullet) other.userData).isDead && ((Bullet) other.userData).top ) {
                if( invulnerable )
                    return null;
                isHit = System.currentTimeMillis() + 100;
                bullet = (Bullet) other.userData;
                bullet.forceFree();
                life --;
            } else if( other.userData instanceof Ship ) {
                ship = (Ship) other.userData;
                if( System.currentTimeMillis() > ship.effect )
                    ship.life --;
                life = 0;
            }
            return null;
        };
    };

    int life;

    AtlasRegion shipRegion;
    //Array<Vector2> pattern;
    Animation<AtlasRegion> explosionAnimation;

    int width = 32, height = 32;
    float screenWidth, screenHeight;
    long time;
    boolean isDead = true;
    public boolean invulnerable = true;
    long isHit;
    
    public EnemyShip( World<Entity> world, Array<AtlasRegion> explosionRegions ) {
        this.world = world;
        item = new Item<Entity>( this );
        world.add( item, 0, 0, 0, 0 );
        explosionAnimation = new Animation<>( .15f, explosionRegions );
        explosionAnimation.setPlayMode( PlayMode.NORMAL );
    }

    public void init( AtlasRegion shipRegion, float[] pattern ) {
        this.shipRegion = shipRegion;
        life = 4;
        invulnerable = true;
        screenWidth = getStage().getWidth();
        screenHeight = getStage().getHeight();

        setBounds( pattern[0] * screenWidth, pattern[1] * screenHeight, width, height );
        world.update( item, pattern[0] * screenWidth, pattern[1] * screenHeight, width, height );
        isDead = false;
        clearActions();
        SequenceAction sequenceAction = new SequenceAction();
        for( int x = 0; x < pattern.length - 1; x += 2 ) {
            final int y = x + 1;
            sequenceAction.addAction( Actions.moveTo( (pattern[x] * screenWidth) - getWidth()/2f, (pattern[y] * screenHeight) - getHeight()/2f, .5f ) );
            sequenceAction.addAction( new Action() {
                @Override
                public boolean act( float delta ) {
                    if( y > 4 )
                        invulnerable = false;
                    if( getStage() != null )
                        Controllers.getInstance().bulletController.fire( getStage(), ( getX() + getWidth()/2f ) - Bullet.width/2, getY() + 5f, 0, 0, false );
                    return true;
                }
            } );
        }
        /*setBounds( pattern.first().x * screenWidth, pattern.first().y * screenHeight, width, height );
        world.update( item, pattern.first().x * screenWidth, pattern.first().y * screenHeight, width, height );
        isDead = false;
        clearActions();
        SequenceAction sequenceAction = new SequenceAction();
        for ( Vector2 patternPos : pattern ) {
            sequenceAction.addAction( Actions.moveTo( (patternPos.x * screenWidth) - getWidth()/2f, (patternPos.y * screenHeight) - getHeight()/2f, .5f ) );
            sequenceAction.addAction( new Action() {
                @Override
                public boolean act(float delta) {
                    if( getStage() != null )
                    Controllers.getInstance().bulletController.fire( getStage(), ( getX() + getWidth()/2f ) - Bullet.width/2, getY() + 5f, false );
                    return true;
                }
            } );
        }*/
        addAction( Actions.forever( sequenceAction ) );
    }

    float deltaTime;
    Random random = new Random();

    @Override
    public void act(float delta) {
        deltaTime += delta;

        if( isDead )
            return;
        super.act(delta);

        world.move( item, getX(), getY(), collisionFilter );
        
        if( life <= 0 ) {
            if( !SoundController.getInstance().explosion.isPlaying() )
                SoundController.getInstance().explosion.play();
            Rumble.rumble( 4f, .4f );
            clearActions();
            isDead = true;
            deltaTime = 0;
            Controllers.getInstance().powerUpController.pop( getStage(), ((random.nextInt(8) + 1)*0.1f) * getStage().getWidth()/*(getX() + getWidth()/2f) + PowerUp.width/2f*/, /*(getY() + getHeight()/2f)*/ getStage().getHeight() + PowerUp.height/2f );
            return;
        }

        /*if( getStage() != null )
            if( time < System.currentTimeMillis() && getY() > getStage().getHeight()/2f ) {
                time = System.currentTimeMillis() + ( random.nextInt( 10 ) + 5 ) * 100;
                Controllers.getInstance().bulletController.fire( getStage(), ( getX() + getWidth()/2f ) - Bullet.width/2, getY() + 5f, false );
            }*/
    }
    Color color = new Color();
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        //if( !isDead )
        batch.draw( shipRegion, getX(), getY(), getWidth(), getHeight() );
        if( isHit > System.currentTimeMillis() || isDead ) {
            color.set( batch.getColor() );
            batch.setColor( 1, 0, 0, .5f );
            batch.draw( shipRegion, getX(), getY(), getWidth(), getHeight() );
            batch.setColor( color );
        }

        if( isDead ) {
            batch.draw( explosionAnimation.getKeyFrame( deltaTime ), getX() - getWidth()/2f, getY() - getHeight()/2f, getWidth()*2f, getHeight()*2f );
            if( explosionAnimation.isAnimationFinished( deltaTime ) ) {
                forceFree();
            }
        }
    }

    @Override
    public void dispose() {
        
    }

    public void forceFree() {
        Controllers.getInstance().enemyController.forceFree( this );
    }

    @Override
    public void reset() {
        clearActions();
        isDead = true;
        setPosition( -100, -100 );
        world.update( item, -100, -100 );
        remove();
    }

}
