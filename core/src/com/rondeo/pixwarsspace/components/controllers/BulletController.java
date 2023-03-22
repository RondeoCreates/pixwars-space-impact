package com.rondeo.pixwarsspace.components.controllers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.components.entity.Bullet;

public class BulletController implements Disposable {
    World<Entity> world;
    AtlasRegion[] bulletRegion;

    public BulletController( World<Entity> world, AtlasRegion... bulletRegion ) {
        this.world = world;
        this.bulletRegion = bulletRegion;
    }

    Array<Bullet> activeBullets = new Array<>();

    private final Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet( world, -10, -10, bulletRegion );
        }
    };

    Bullet bullet;
    public void fire( Stage stage, float x, float y, float dirX, float dirY, boolean top ) {
        bullet = bulletPool.obtain();
        bullet.init( x, y, top );
        stage.addActor( bullet );
        bullet.dirX = dirX;
        bullet.dirY = dirY;
        //activeBullets.add( bullet );
        //System.out.println( bulletPool.peak );
    }

    public void forceFree( Bullet item ) {
        bulletPool.free( item );
        item.dispose();
        //activeBullets.removeValue( item, false );
    }

    @Override
    public void dispose() {
        for ( Bullet shipBullet : activeBullets ) {
            shipBullet.dispose();
        }
    }

}
