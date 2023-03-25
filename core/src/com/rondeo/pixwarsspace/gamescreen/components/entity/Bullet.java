package com.rondeo.pixwarsspace.gamescreen.components.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Rect;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.dongbat.jbump.Response.Result;
import com.rondeo.pixwarsspace.gamescreen.components.Controllers;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.gamescreen.components.Outbound;
import com.rondeo.pixwarsspace.utils.SoundController;

public class Bullet extends Actor implements Entity, Disposable, Poolable {
    World<Entity> world;
    Item<Entity> item;
    Rect rect;
    Result result;
    Collision collision;
    CollisionFilter collisionFilter = new CollisionFilter() {
        @SuppressWarnings( "rawtypes" )
        @Override
        public Response filter(Item item, Item other) {
            /*if( other.userData instanceof Outbound ) {
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
            }*/
            if( other.userData instanceof Outbound )
                return Response.cross;
            if( other.userData instanceof BossParts && top )
                return Response.cross;
            if( other.userData instanceof Ship && !top  )
                return Response.cross;
            if( other.userData instanceof EnemyShip && top && !((EnemyShip)other.userData).invulnerable && ((EnemyShip)other.userData).life > 0 )
                return Response.cross;
            if( other.userData instanceof Bullet && ((Bullet)other.userData).top != top )
                return Response.cross;
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
        //System.out.println( dirX + "<>" + ( (top ? 300 : -300) * delta) + dirY );
        result = world.move( item, getX() + dirX, getY() + ( (top ? 300 : -300) * delta) + dirY, collisionFilter );
        for( int i = 0; i < result.projectedCollisions.size(); i ++ ) {
            collision = result.projectedCollisions.get( i );
            if( collision.other.userData instanceof Outbound || collision.other.userData instanceof Bullet ) {
                forceFree();
                return;
            }
            if( collision.other.userData instanceof BossParts ) {
                if( ((BossParts)collision.other.userData).life > 0 ) {
                    if( ((BossParts)collision.other.userData).isHit + 100 < System.currentTimeMillis()  ) {
                        ((BossParts)collision.other.userData).isHit = System.currentTimeMillis() + 100;
                        SoundController.play( SoundController.getInstance().hurt );
                        ((BossParts)collision.other.userData).reduceLife();
                    }
                    forceFree();
                    return;
                }
            }
            if( collision.other.userData instanceof Ship ) {
                if( System.currentTimeMillis() > ((Ship)collision.other.userData).isHit + ((Ship)collision.other.userData).invTime && System.currentTimeMillis() >((Ship)collision.other.userData).invulnerable ) {
                    SoundController.play( SoundController.getInstance().hurt );
                    ((Ship)collision.other.userData).isHit = System.currentTimeMillis() + 300;
                    ((Ship)collision.other.userData).life --;
                }
                forceFree();
                return;
            }
            if( collision.other.userData instanceof EnemyShip ) {
                ((EnemyShip)collision.other.userData).isHit = System.currentTimeMillis() + 100;
                ((EnemyShip)collision.other.userData).life --;
                forceFree();
                return;
            }
        }
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
