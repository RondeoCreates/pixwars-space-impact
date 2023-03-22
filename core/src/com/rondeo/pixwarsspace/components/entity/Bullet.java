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
import com.rondeo.pixwarsspace.components.controllers.BossController;
import com.rondeo.pixwarsspace.components.controllers.SoundController;

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
                return null;
            }
            if( other.userData instanceof BossController.BossParts && top ) {
                BossController.BossParts part = (BossController.BossParts) other.userData;
                if( part.life > 0 ) {
                    if( part.isHit + 100 < System.currentTimeMillis() ) {
                        part.isHit = System.currentTimeMillis() + 100;
                        SoundController.getInstance().hurt.play();
                        part.reduceLife();
                        forceFree();
                    }
                    //forceFree();
                }
            }
            
            // else if( other.userData instanceof Bullet && ((Bullet)other.userData).top != top ) {
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
        isDead = false;
        world.update( item, x, y, width, height );
        resolve();
        this.top = top;
    }

    @Override
    public void act( float delta ) {
        if( isDead )
            return;
        super.act(delta);
        //moveBy( dirX, ( (top ? 100 : -100) * delta) + dirY );
        world.move( item, getX() + dirX, getY() + ( (top ? 300 : -300) * delta) + dirY, collisionFilter );
        //world.move( item, getX(), getY(), collisionFilter );
        resolve();
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
        Controllers.getInstance().bulletController().forceFree( this );
    }

    @Override
    public void reset() {
        isDead = true;
        world.update( item, -10, -10 );
        resolve();
        remove();
    }

    public void resolve() {
        rect = world.getRect( item );
        setBounds( rect.x, rect.y, rect.w, rect.h );
    }

}
