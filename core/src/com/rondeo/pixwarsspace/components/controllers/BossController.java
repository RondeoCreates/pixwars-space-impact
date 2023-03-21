package com.rondeo.pixwarsspace.components.controllers;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.components.Entity;

public class BossController extends Actor implements Disposable {
    Animation<AtlasRegion> tentacleAnimation;
    AtlasRegion headRegion, handRegion;
    public static final int width = 200, height = 200;

    enum Attacks {
        SHOOT_SHIPS,
        HAND_LASER,
        EYE_LASER
    };

    public BossController( World<Entity> world, Array<AtlasRegion> tentacleRegions, AtlasRegion headRegion, AtlasRegion handRegion ) {
        tentacleAnimation = new Animation<>( .15f, tentacleRegions );
        tentacleAnimation.setPlayMode( PlayMode.LOOP );
        this.headRegion = headRegion;
        this.handRegion = handRegion;
    }

    public void setup() {
        setBounds( 0, getStage().getHeight() - height, width, height );
    }

    public void setupHands() {

    }

    public void setupHead() {

    }

    public void setupTentacle() {
        
    }

    float deltaTime;
    @Override
    public void act(float delta) {
        super.act(delta);
        deltaTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw( headRegion, getX(), getY(), getWidth(), getHeight() );
        batch.draw( tentacleAnimation.getKeyFrame( deltaTime ), getX(), getY(), getWidth(), getHeight() );
        batch.draw( handRegion, getX(), getY(), getWidth(), getHeight() );
    }

    @Override
    public void dispose() {
        
    }

}
