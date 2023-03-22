package com.rondeo.pixwarsspace.components;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.utils.Disposable;

public class Background extends Actor implements Disposable {
    Texture bgTexture0, bgTexture1, bgTexture2;
    TextureRegion bgRegion0, bgRegion1, bgRegion2, bg;
    Entity.Wrapper ship;
    TextureAtlas assets;

    public Background( int width, int height, Entity.Wrapper ship, TextureAtlas assets ) {
        this.assets = assets;
        bg = assets.findRegion( "bg" );

        bgTexture0 = new Texture( Gdx.files.internal( "input/bg_00.png" ) );
        bgTexture0.setWrap( TextureWrap.ClampToEdge, TextureWrap.Repeat );

        bgTexture1 = new Texture( Gdx.files.internal( "input/bg_01.png" ) );
        bgTexture1.setWrap( TextureWrap.ClampToEdge, TextureWrap.Repeat );

        bgTexture2 = new Texture( Gdx.files.internal( "input/bg_02.png" ) );
        bgTexture2.setWrap( TextureWrap.ClampToEdge, TextureWrap.Repeat );

        bgRegion0 = new TextureRegion( bgTexture0 );
        bgRegion1 = new TextureRegion( bgTexture1 );
        bgRegion2 = new TextureRegion( bgTexture2 );

        update( 0, 0, width, height );
        this.ship = ship;
    }

    float yOffset = 0;
    float yOffset2 = 0;
    @Override
    public void act(float delta) {
        super.act(delta);
        yOffset -= delta * 400;
        yOffset2 -= delta * 20;

        bg.scroll( 0, yOffset );
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw( batch, parentAlpha );
        batch.draw( bg, getX(), getY(), getWidth(), getHeight() );
        //batch.draw( bgTexture0, getX(), getY(), getWidth(), getHeight(), 0, 0, (int) (getWidth() + 1), (int) (getHeight() + 1), false, false );
        //batch.draw( bgTexture1, getX(), getY(), getWidth(), getHeight(), 0, (int) yOffset2, (int) (getWidth() + 1), (int) (getHeight() + 1), false, false );
        //batch.draw( bgTexture2, getX(), getY(), getWidth(), getHeight(), 0, (int) yOffset, (int) (getWidth() + 1), (int) (getHeight() + 1), false, false );

        if( ship.isHit > System.currentTimeMillis() || Controllers.getInstance().gameOver ) {
            batch.setColor( 1, 0, 0, .6f );
        } else {
            batch.setColor( Color.WHITE );
        }
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        update( x, y, width, height );
    }

    public void update( float x, float y, float width, float height ) {
        bgRegion0.setRegion( x, y, width, height);
        bgRegion1.setRegion( x, y, width, height);
        bgRegion2.setRegion( x, y, width, height);
    }

    @Override
    public void dispose() {
        bgTexture0.dispose();
        bgTexture1.dispose();
        bgTexture2.dispose();
    }
    
}
