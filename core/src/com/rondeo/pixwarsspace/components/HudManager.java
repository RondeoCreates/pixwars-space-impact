package com.rondeo.pixwarsspace.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.rondeo.pixwarsspace.GameScreen;
import com.rondeo.pixwarsspace.Main;
import com.rondeo.pixwarsspace.components.controllers.SoundController;

public class HudManager implements Disposable {
    public Stage hud;
    Skin skin;
    Table table;
    TextureAtlas assets;
    TutorialManager tutorialManager;
    int tutType = -1;

    public HudManager( final Main main, TextureAtlas assets ) {
        this.assets = assets;

        hud = new Stage( new FitViewport( main.width*1.5f , main.height*1.5f ) );
        skin = new Skin( Gdx.files.internal( "ui/default.json" ) );

        Label label = new Label("Game\nOver", skin, "big");


        TextButton restartButton = new TextButton( "RESTART", skin );
        restartButton.addListener( new InputListener() {
            public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
                return true;
            };
            public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
                Controllers.getInstance().gameOver = false;
                SoundController.getInstance().select.play();
                main.setScreen( new GameScreen( main ) );
            };
        } );
        
        table = new Table();
        table.setFillParent(true);
        table.setVisible( false );


        table.add().expandY();

        table.row();
        table.add( label );

        table.row().padTop( 100 );
        table.add( restartButton ).fillX();

        table.row();
        table.add().expandY();

        table.row();
        table.add();

        hud.addActor( table );

        tutorialManager = new TutorialManager();
    }

    public void update() {
        hud.getViewport().apply();
        hud.act();
        hud.draw();
    }

    public void gameOver() {
        table.setVisible( true );
    }

    public void showTutorial() {
        tutorialManager.show( Controllers.getInstance().tutorial );
    }

    @Override
    public void dispose() {
        hud.dispose();
        skin.dispose();
    }

    private class TutorialManager {

        public String[] tutTitle = {
            "Power Up - Wing",
            "Power Up - Shield",
            "Power Up - Health",
            "Power Up - Halo"
        };
        public String[] tutString = {
            "Shoots two bullets in each\n" + 
                "fire instead of one",
            "Invulnerable to bullets",
            "Increases health by one",
            "Can tank, invulnerable to\n" +
                "other enemy ship when crashed.\n" +
                "But vulnerable to bullets"
        };
        public String[] tutPreview = {
            "wing",
            "ship_sketch",
            "ship_sketch",
            "effect"
        };

        Image ship, preview, img;
        Label title, message;
        Window window;
        public TutorialManager() {
            window = new Window( "", skin );
            window.pad( 15f );
            window.setVisible( false );
            window.setModal( true );
            window.setSize( hud.getWidth(), 320 );
            window.setPosition( 0, hud.getHeight()/2f - window.getHeight()/2f );

            img = new Image();
            ship = new Image( assets.findRegion( "ship" ) );
            preview = new Image();

            title = new Label( "", skin );
            title.setAlignment( Align.center );
            message = new Label( "", skin );
            message.setAlignment( Align.center );

            TextButton closeButton = new TextButton( "COPY THAT", skin );
            closeButton.addListener( new InputListener() {
                public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
                    return true;
                };
                public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
                    SoundController.getInstance().select.play();
                    Controllers.getInstance().pause = false;
                    window.setVisible( false );
                    Controllers.getInstance().powerUp( tutType );
                };
            } );

            Stack stack = new Stack();
            stack.add( ship );
            stack.add( preview );

            window.add( title );
            window.row().padTop( 20 );
            window.add( img ).size( 30 );
            window.row().padBottom( 20 );
            window.add( stack ).size( 60 );
            window.row();
            window.add( message );
            window.row();
            window.add().expandY();
            window.row();
            window.add( closeButton );

            hud.addActor( window );
        }

        public void show( int tut ) {
            tutType = tut;
            title.setText( tutTitle[tut] );
            message.setText( tutString[tut] );
            img.setDrawable( new TextureRegionDrawable( assets.findRegions( "powerups" ).get(tut) ) );
            preview.setDrawable( new TextureRegionDrawable( assets.findRegion( tutPreview[tut] ) ) );
            window.setVisible( true );
            Controllers.getInstance().tutorial = -1;
        }
    }
}
