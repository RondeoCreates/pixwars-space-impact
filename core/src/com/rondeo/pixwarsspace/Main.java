package com.rondeo.pixwarsspace;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.rondeo.pixwarsspace.components.controllers.SoundController;

public class Main extends Game {
    public int width = 200;
    public int height = 400;
    Pixmap curPixmap;
        
    @Override
    public void create () {
        curPixmap = new Pixmap( Gdx.files.internal( "input/cursor.png" ) );
        Gdx.graphics.setCursor( Gdx.graphics.newCursor( curPixmap, 0, 0 ) );
        SoundController.getInstance().bgm.play();
        SoundController.getInstance().intro.play();
        setScreen( new GameScreen( this ) );
    }

    @Override
    public void dispose() {
        super.dispose();
        curPixmap.dispose();
    }

}
