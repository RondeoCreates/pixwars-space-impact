package com.rondeo.pixwarsspace.components.controllers;

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
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.utils.Rumble;

public class BossController extends Actor implements Disposable {
    Animation<AtlasRegion> tentacleAnimation, explosionAnimation;
    AtlasRegion headRegion, leftHandRegion, rightHandRegion, leftLaserRegion, rightLaserRegion, headLaserRegion;
    public static final int width = 200, height = 200;
    public long timeStart = 0;
    public boolean boss = false;
    private World<Entity> world;
    private Item<Entity> leftHand, rightHand, tentacle, head;
    private Head headEntity;
    private LeftHand leftHandEntity;
    private RightHand rightHandEntity;
    private Tentacle tentacleEntity;

    enum Attacks {
        SHOOT_SHIPS,
        HAND_LASER,
        EYE_LASER
    };

    public BossController( World<Entity> world, Array<AtlasRegion> tentacleRegions, Array<AtlasRegion> exposionRegions, AtlasRegion headRegion, AtlasRegion leftHandRegion, AtlasRegion rightHandRegion, AtlasRegion leftLaserRegion, AtlasRegion rightLaserRegion, AtlasRegion headLaserRegion ) {
        tentacleAnimation = new Animation<>( .15f, tentacleRegions );
        explosionAnimation = new Animation<>( .15f, exposionRegions );
        tentacleAnimation.setPlayMode( PlayMode.LOOP );
        explosionAnimation.setPlayMode( PlayMode.NORMAL );
        this.headRegion = headRegion;
        this.leftHandRegion = leftHandRegion;
        this.rightHandRegion = rightHandRegion;
        this.leftLaserRegion = leftLaserRegion;
        this.rightLaserRegion = rightLaserRegion;
        this.headLaserRegion = headLaserRegion;
        this.world = world;
    }

    public void setup() {
        setBounds( 0, getStage().getHeight() - height, width, height );
        timeStart = System.currentTimeMillis();// + 1 * 60 * 1000;
    }

    public void setupHands() {
        NinePatch nine = new NinePatch( leftLaserRegion, 24, 174, 54, 0 );
        leftHand = new Item<Entity>( leftHandEntity = new LeftHand( 0, getStage().getHeight() - 60, getStage().getWidth()/4f, 60, new NinePatchDrawable( nine ) ) );
        
        NinePatch nine2 = new NinePatch( rightLaserRegion, 173, 25, 54, 0 );
        rightHand = new Item<Entity>( rightHandEntity = new RightHand( (getStage().getWidth()/4f) * 3, getStage().getHeight() - 60, getStage().getWidth()/4f, 60, new NinePatchDrawable( nine2 ) ) );

        world.add( leftHand, 0, getStage().getHeight() - 60, getStage().getWidth()/4f, 60 );
        world.add( rightHand, (getStage().getWidth()/4f) * 3, getStage().getHeight() - 60, getStage().getWidth()/4f, 60 );
    }

    public void setupHead() {
        NinePatch nine = new NinePatch( headLaserRegion, 98, 99, 14, 0 );
        head = new Item<Entity>( headEntity = new Head( getStage().getWidth()/4f, getStage().getHeight() - 80, getStage().getWidth()/2f, 80, new NinePatchDrawable( nine ) ) );
        world.add( head, getStage().getWidth()/4f, getStage().getHeight() - 80, getStage().getWidth()/2f, 80 );
    }

    public void setupTentacle() {
        tentacle = new Item<Entity>( tentacleEntity = new Tentacle( getStage().getWidth()/4f, getStage().getHeight() - 150, getStage().getWidth()/2f, 50 ) );
        world.add( tentacle, getStage().getWidth()/4f, getStage().getHeight() - 150, getStage().getWidth()/2f, 50 );
    }

    float deltaTime;
    @Override
    public void act(float delta) {
        super.act(delta);
        deltaTime += delta;
        if( !boss ) {
            setupHead();
            setupTentacle();
            setupHands();
            if( System.currentTimeMillis() > timeStart + 1000 )
                boss = true;
        }
    }

    Color color = new Color();
    @Override
    public void draw( Batch batch, float parentAlpha ) {
        if( !boss )
            return;
        super.draw(batch, parentAlpha);
        
        headEntity.draw( batch, headRegion );
        tentacleEntity.draw( batch, tentacleAnimation.getKeyFrame( deltaTime ) );
        leftHandEntity.draw( batch, leftHandRegion );
        rightHandEntity.draw( batch, rightHandRegion );
        
    }

    @Override
    public void dispose() {
        
    }

    public class BossParts {
        public boolean isDead = false;
        public long isHit = 0;
        public int life = 10;

        public float x, y, width, height;

        public BossParts( float x, float y, float width, float height ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void reduceLife() {
            life --;
            if( life <= 0 )
                deltaTime = 0;
        }

        public void draw( Batch batch, AtlasRegion region  ) {
            if( isDead )
                return;
            if( !isDead ) {
                batch.draw( region, getX(), getY(), getWidth(), getHeight() );
                if( (isHit > System.currentTimeMillis() && life > 0) || life <= 0 ) {
                    color.set( batch.getColor() );
                    batch.setColor( 1, 0, 0, 1f );
                    batch.draw( region, getX(), getY(), getWidth(), getHeight() );
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

    private class Head extends BossParts implements Entity {
        NinePatchDrawable ninePatchDrawable;

        public Head( float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable ) {
            super( x, y, width, height );
            this.ninePatchDrawable = ninePatchDrawable;
        }

        @Override
        public void reduceLife() {
            if( leftHandEntity.life <= 0 && rightHandEntity.life <= 0 )
                super.reduceLife();
        }

        @Override
        public void draw(Batch batch, AtlasRegion region) {
            super.draw( batch, region );
            //ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
        }
    }

    private class LeftHand extends BossParts implements Entity {
        NinePatchDrawable ninePatchDrawable;

        public LeftHand(float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable ) {
            super( x, y, width, height );
            this.ninePatchDrawable = ninePatchDrawable;
        }

        @Override
        public void reduceLife() {
            if( tentacleEntity.life <= 0 )
                super.reduceLife();
        }

        @Override
        public void draw(Batch batch, AtlasRegion region) {
            super.draw( batch, region );
            //ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
        }
    }

    private class RightHand extends BossParts implements Entity {
        NinePatchDrawable ninePatchDrawable;

        public RightHand( float x, float y, float width, float height, NinePatchDrawable ninePatchDrawable ) {
            super( x, y, width, height );
            this.ninePatchDrawable = ninePatchDrawable;
        }

        @Override
        public void reduceLife() {
            if( tentacleEntity.life <= 0 )
                super.reduceLife();
        }

        @Override
        public void draw( Batch batch, AtlasRegion region ) {
            super.draw( batch, region );
            //ninePatchDrawable.draw( batch, 0, 0, getStage().getWidth(), getStage().getHeight() );
        }
    }

    private class Tentacle extends BossParts implements Entity {

        public Tentacle( float x, float y, float width, float height ) {
            super( x, y, width, height );
        }

    }

}
