package com.rondeo.pixwarsspace.gamescreen.components.controllers;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.gamescreen.components.Controllers;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.gamescreen.components.entity.BossParts;
import com.rondeo.pixwarsspace.gamescreen.components.entity.Bullet;

public class BossController extends Actor implements Disposable {
    Animation<AtlasRegion> tentacleAnimation;
    private Array<AtlasRegion> explosionRegions;
    AtlasRegion headRegion, leftHandRegion, rightHandRegion, leftLaserRegion, rightLaserRegion, headLaserRegion, warningRegion;
    public static final int width = 200, height = 200;
    public long timeStart = 0;
    public boolean boss = false, dead = false;
    private World<Entity> world;
    private Head headEntity;
    private LeftHand leftHandEntity;
    private RightHand rightHandEntity;
    private Tentacle tentacleEntity;

    Array<BossParts> bossParts = new Array<>();
    long nextAttack = 0;
    long warning = 0;
    BossParts attack = null;
    Random random = new Random();

    public BossController( World<Entity> world, Array<AtlasRegion> tentacleRegions, Array<AtlasRegion> exposionRegions, AtlasRegion headRegion, AtlasRegion leftHandRegion, AtlasRegion rightHandRegion, AtlasRegion leftLaserRegion, AtlasRegion rightLaserRegion, AtlasRegion headLaserRegion, AtlasRegion warningRegion ) {
        tentacleAnimation = new Animation<>( .15f, tentacleRegions );
        tentacleAnimation.setPlayMode( PlayMode.LOOP );
        this.explosionRegions = exposionRegions;
        this.headRegion = headRegion;
        this.leftHandRegion = leftHandRegion;
        this.rightHandRegion = rightHandRegion;
        this.leftLaserRegion = leftLaserRegion;
        this.rightLaserRegion = rightLaserRegion;
        this.headLaserRegion = headLaserRegion;
        this.warningRegion = warningRegion;
        this.world = world;
    }

    public void setup() {
        setBounds( 0, getStage().getHeight() - height, width, height );
        timeStart = System.currentTimeMillis() + 1 * 60 * 1000; // Change this later
    }

    public void setupHands() {
        NinePatch nine = new NinePatch( leftLaserRegion, 24, 174, 54, 0 );
        leftHandEntity = new LeftHand( world, 0, getStage().getHeight() - 60, getStage().getWidth()/4f, 60, new NinePatchDrawable( nine ), explosionRegions );
        bossParts.add( leftHandEntity );

        NinePatch nine2 = new NinePatch( rightLaserRegion, 173, 25, 54, 0 );
        rightHandEntity = new RightHand( world, (getStage().getWidth()/4f) * 3, getStage().getHeight() - 60, getStage().getWidth()/4f, 60, new NinePatchDrawable( nine2 ), explosionRegions );
        bossParts.add( rightHandEntity );
    }

    public void setupHead() {
        NinePatch nine = new NinePatch( headLaserRegion, 98, 99, 14, 0 );
        headEntity = new Head( world, getStage().getWidth()/4f, getStage().getHeight() - 50, getStage().getWidth()/2f, 80, new NinePatchDrawable( nine ), explosionRegions );
        bossParts.add( headEntity );
    }

    public void setupTentacle() {
        tentacleEntity = new Tentacle( world, getStage().getWidth()/5f, getStage().getHeight() - 100, (getStage().getWidth()/5f)*3f, 50, explosionRegions );
        bossParts.add( tentacleEntity );
    }

    float deltaTime;
    float delta;
    long time = 0;
    @Override
    public void act(float delta) {
        super.act(delta);
        this.delta = delta;
        deltaTime += delta;
        if( !boss ) {
            if( System.currentTimeMillis() > timeStart + 4000 ) {
                setupHead();
                setupTentacle();
                setupHands();
                boss = true;
                Controllers.getInstance().getShip().effect = System.currentTimeMillis() + 8000;
                Controllers.getInstance().getShip().hasWings = System.currentTimeMillis() + 15000;
                Controllers.getInstance().getShip().invulnerable = System.currentTimeMillis() + 5000;
                Controllers.getInstance().getShip().life ++;
                attack = tentacleEntity;
            }
        } else {
            if( nextAttack + 3000 < System.currentTimeMillis() || attack == null ) {
                if( bossParts.size > 0 ) {
                    attack = bossParts.get( random.nextInt( bossParts.size ) );
                    //if( attack instanceof Head )
                    //    attack = tentacleEntity;*/
                    nextAttack = System.currentTimeMillis() + 5000;
                    warning = System.currentTimeMillis() + 3000;
                } else {
                    dead = true;
                }
            }
        }

        if( attack != null && attack.life <= 0 ) {
            bossParts.removeValue( attack, false );
            attack = null;
            return;
        }
    }

    Color color = new Color();
    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( !boss )
            return;
        super.draw(batch, parentAlpha);
        
        headEntity.draw( this, batch, headRegion, delta );
        tentacleEntity.draw( this, batch, tentacleAnimation.getKeyFrame( deltaTime ), delta );
        leftHandEntity.draw( this, batch, leftHandRegion, delta );
        rightHandEntity.draw( this, batch, rightHandRegion, delta );
        
        if( attack != null ) {
            if( System.currentTimeMillis() > nextAttack ) {
                attack.attack( batch );
            } else if( System.currentTimeMillis() > warning ) {
                attack.warning( batch, warningRegion );
            }
        }
    }

    @Override
    public void dispose() {
        
    }

    private class Head extends BossParts {
        NinePatchDrawable ninePatchDrawable;
        Item<Entity> laserItem;
        Laser laser;
        float x, y, width, height;

        public Head( World<Entity> world, float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable, Array<AtlasRegion> explosionRegions ) {
            super( world, x, y, width, height, explosionRegions );
            life = 100;
            this.ninePatchDrawable = ninePatchDrawable;
            
            this.x = 93;
            this.y = 0;
            this.width = 14;
            this.height = getStage().getHeight();
            laserItem = new Item<Entity>( laser = new Laser() );
            world.add( laserItem, this.x, this.y, this.width, this.height );
        }

        @Override
        public void reduceLife() {
            if( leftHandEntity.life <= 0 && rightHandEntity.life <= 0 && tentacleEntity.life <= 0 )
                super.reduceLife();
        }

        @Override
        public void draw(Actor actor, Batch batch, AtlasRegion region, float delta) {
            super.draw(actor, batch, region, delta);
            laser.active = false;
        }

        @Override
        public void attack( Batch batch ) {
            ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
            laser.active = true;
        }

        @Override
        public void warning( Batch batch, AtlasRegion atlasRegion ) {
            batch.draw( atlasRegion, x - 10 , y, width + 20 , height );
        }
    }

    private class LeftHand extends BossParts {
        NinePatchDrawable ninePatchDrawable;
        Item<Entity> laserItem;
        Laser laser;
        float x, y, width, height;

        public LeftHand( World<Entity> world, float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable, Array<AtlasRegion> explosionRegions ) {
            super( world, x, y, width, height, explosionRegions );
            this.ninePatchDrawable = ninePatchDrawable;

            this.x = 20;
            this.y = 0;
            this.width = 10;
            this.height = getStage().getHeight();
            laserItem = new Item<Entity>( laser = new Laser() );
            world.add( laserItem, this.x, this.y, this.width, this.height );
        }

        @Override
        public void reduceLife() {
            super.reduceLife();
        }

        @Override
        public void draw(Actor actor, Batch batch, AtlasRegion region, float delta) {
            super.draw(actor, batch, region, delta);
            laser.active = false;
        }

        @Override
        public void attack( Batch batch ) {
            ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
            laser.active = true;
        }

        @Override
        public void warning( Batch batch, AtlasRegion atlasRegion ) {
            batch.draw( atlasRegion, x - 10 , y, width + 20 , height );
        }
    }

    private class RightHand extends BossParts {
        NinePatchDrawable ninePatchDrawable;
        Item<Entity> laserItem;
        Laser laser;
        float x, y, width, height;

        public RightHand( World<Entity> world, float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable, Array<AtlasRegion> explosionRegions ) {
            super( world, x, y, width, height, explosionRegions );
            this.ninePatchDrawable = ninePatchDrawable;

            this.x = 169;
            this.y = 0;
            this.width = 10;
            this.height = getStage().getHeight();
            laserItem = new Item<Entity>( laser = new Laser() );
            world.add( laserItem, this.x, this.y, this.width, this.height );
        }

        @Override
        public void reduceLife() {
            super.reduceLife();
        }

        @Override
        public void draw(Actor actor, Batch batch, AtlasRegion region, float delta) {
            super.draw(actor, batch, region, delta);
            laser.active = false;
        }

        @Override
        public void attack( Batch batch ) {
            ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
            laser.active = true;
        }

        @Override
        public void warning( Batch batch, AtlasRegion atlasRegion ) {
            batch.draw( atlasRegion, x - 10 , y, width + 20 , height );
        }
    }

    public class Tentacle extends BossParts {

        long time = 0;
        public Tentacle( World<Entity> world, float x, float y, float width, float height, Array<AtlasRegion> explosionRegions ) {
            super( world, x, y, width, height, explosionRegions );
            life = 200;
        }

        @Override
        public void attack(Batch batch) {
            if( System.currentTimeMillis() > time + 500 && ( !Controllers.getInstance().gameOver && !Controllers.getInstance().pause ) ) {
                time = System.currentTimeMillis();
                for( int i = -3; i <= 3; i ++ ) {
                    for( int j = -3; j <= 3; j ++ ) {
                        Controllers.getInstance().bulletController().fire( getStage(), (getX() + getWidth()/2f) - Bullet.width/2f, (getY() + getWidth()/2f) - Bullet.height/2f, (i*.3f), j*.3f, false );
                    }
                }
            }
        }

    }

    public class Laser implements Entity {
        public boolean active = false;
    }

}
