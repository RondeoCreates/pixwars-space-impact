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
    Texture bgTexture;
    TextureRegion bgRegion;
    Entity.Wrapper ship;

    public Background( int width, int height, Entity.Wrapper ship ) {
        bgTexture = new Texture( Gdx.files.internal( "input/bg.png" ) );
        bgTexture.setWrap( TextureWrap.ClampToEdge, TextureWrap.Repeat );
        bgRegion = new TextureRegion( bgTexture );
        update( 0, 0, width, height );
        this.ship = ship;
    }

    float yOffset = 0;
    @Override
    public void act(float delta) {
        super.act(delta);
        yOffset -= delta * 100;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.draw( bgTexture, getX(), getY(), getWidth(), getHeight(), 0, (int) yOffset, (int) (getWidth() + 1), (int) (getHeight() + 1), false, false );
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
        bgRegion.setRegion( x, y, width, height);
    }

    @Override
    public void dispose() {
        bgTexture.dispose();
    }
    
}
