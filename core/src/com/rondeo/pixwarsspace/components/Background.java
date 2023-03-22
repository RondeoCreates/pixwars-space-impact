package com.rondeo.pixwarsspace.components;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.Disposable;

public class Background extends Actor implements Disposable {
    Texture bg;
    TextureRegion bgRegion0, bgRegion1, bgRegion2;
    Entity.Wrapper ship;

    public Background( int width, int height, Entity.Wrapper ship ) {
        bg = new Texture( Gdx.files.internal( "bg/bg.png" ) );
        bg.setWrap( TextureWrap.ClampToEdge, TextureWrap.Repeat );

        bgRegion0 = new TextureRegion( bg, 0, 0, 200, 400 );
        bgRegion1 = new TextureRegion( bg, 200, 0, 200, 400 );
        bgRegion2 = new TextureRegion( bg, 400, 0, 200, 400 );

        this.ship = ship;
    }
    @Override
    public void act(float delta) {
        super.act(delta);

        bgRegion2.scroll( 0, -(delta/2f) );
        bgRegion1.scroll( 0, -(delta/40f) );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw( batch, parentAlpha );

        batch.draw( bgRegion0, 0, 0, getStage().getWidth(), getStage().getHeight() );
        batch.draw( bgRegion1, 0, 0, getStage().getWidth(), getStage().getHeight() );
        batch.draw( bgRegion2, 0, 0, getStage().getWidth(), getStage().getHeight() );

        if( ship.isHit > System.currentTimeMillis() || Controllers.getInstance().gameOver ) {
            batch.setColor( 1, 0, 0, .6f );
            //System.out.println( Gdx.graphics.getFramesPerSecond() );
        } else {
            batch.setColor( Color.WHITE );
        }
    }

    @Override
    public void dispose() {
        bg.dispose();
    }
    
}
