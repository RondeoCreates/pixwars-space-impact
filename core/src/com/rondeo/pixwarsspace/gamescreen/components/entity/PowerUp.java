package com.rondeo.pixwarsspace.gamescreen.components.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.gamescreen.components.Controllers;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.gamescreen.components.Outbound;
import com.rondeo.pixwarsspace.utils.SoundController;

public class PowerUp extends Actor implements Entity, Disposable, Poolable {
    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    CollisionFilter collisionFilter = new CollisionFilter() {
        @SuppressWarnings( "rawtypes" )
        @Override
        public Response filter( Item item, Item other ) {
            if( other.userData instanceof Ship ) {
                // Add powerup
                SoundController.play( SoundController.getInstance().health );
                Controllers.getInstance().powerUp( type );
                forceFree();
            } else if( other.userData instanceof Outbound ) {
                forceFree();
            }
            return null;
        };
    };

    AtlasRegion powerRegion;
    boolean isDead = false;
    public int type = -1;
    public static final int width = 24, height = 24;

    public PowerUp( World<Entity> world ) {
        this.world = world;
        item = new Item<Entity>( this );
        world.add( item, -50, -50, width, height );
        setBounds( -50, -50, width, height );
    }

    public void init( AtlasRegion powerRegion, int type, float x, float y ) {
        this.powerRegion = powerRegion;
        this.type = type;
        setBounds( x, y, width, height );
        world.update( item, x, y, width, height );
        isDead = false;
    }

    @Override
    public void act( float delta ) {
        if( isDead )
            return;
        super.act(delta);
        
        world.move( item, getX(), getY() - ( 80 * delta ), collisionFilter );
        rect = world.getRect( item );
        setBounds( rect.x, rect.y, rect.w, rect.h );
    }

    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( isDead )
            return;
        super.draw(batch, parentAlpha);

        batch.draw( powerRegion, getX(), getY(), getWidth(), getHeight() );
    }

    public void forceFree() {
        Controllers.getInstance().powerUpController().forceFree( this );
    }

    @Override
    public void reset() {
        isDead = true;
        setPosition( -50, -50 );
        world.update( item, -50, -50 );
        remove();
    }

    @Override
    public void dispose() {}
    
}
