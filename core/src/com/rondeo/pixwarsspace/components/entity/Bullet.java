package com.rondeo.pixwarsspace.components.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.components.Controllers;
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.components.Outbound;

public class Bullet extends Actor implements Entity, Disposable, Poolable {
    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    CollisionFilter collisionFilter = new CollisionFilter() {
        @SuppressWarnings( "rawtypes" )
        @Override
        public Response filter(Item item, Item other) {
            if( other.userData instanceof Outbound ) {
                forceFree();
            }// else if( other.userData instanceof Bullet && ((Bullet)other.userData).top != top ) {
            //    forceFree();
            //}
            return null;
        }
        
    };

    TextureRegion[] bulletRegion;
    boolean isDead = false;
    boolean top = false;
    float time = 0;
    public float dirX = 0, dirY = 0;
    public static int width = 4, height = 8;

    public Bullet( World<Entity> world, float x, float y, TextureRegion... bulletRegion ) {
        this.world = world;
        item = new Item<Entity>( this );
        world.add( item, x, y, width, height );

        this.bulletRegion = bulletRegion;
        setBounds( x, y, width, height );
    }

    public void init( float x, float y, boolean top ) {
        setBounds( x, y, width, height );
        world.update( item, x, y, width, height );
        isDead = false;
        this.top = top;
    }

    @Override
    public void act( float delta ) {
        if( isDead )
            return;
        super.act(delta);
        
        world.move( item, getX() + dirX, getY() + ( (top ? 300 : -300) * delta) + dirY, collisionFilter );
        rect = world.getRect( item );
        setBounds( rect.x, rect.y, rect.w, rect.h );
    }

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( isDead )
            return;
        super.draw( batch, parentAlpha );
        
        batch.draw( top ? bulletRegion[0] : bulletRegion[1], getX(), getY(), getWidth(), getHeight() );
    }

    @Override
    public void dispose() {
        
    }

    public void forceFree() {
        Controllers.getInstance().bulletController.forceFree( this );
    }

    @Override
    public void reset() {
        isDead = true;
        setPosition( -10, -10 );
        world.update( item, -10, -10 );
        remove();
    }

}
