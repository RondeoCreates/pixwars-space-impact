package com.rondeo.pixwarsspace.gamescreen.components;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.rondeo.pixwarsspace.Main;
import com.rondeo.pixwarsspace.gamescreen.GameScreen;
import com.rondeo.pixwarsspace.menuscreen.MenuScreen;
import com.rondeo.pixwarsspace.utils.SoundController;

public class HudManager implements Disposable {
    public Stage hud;
    Skin skin;
    Table table, credits;
    TextureAtlas assets;
    TutorialManager tutorialManager;
    WarningManager warningManager;
    int tutType = -1;
    TextButton restartButton;
    Label lifeLabel, gameOverLabel, dialogLabel;
    Window lifeWindow;
    private Main main;

    public HudManager( final Main main, TextureAtlas assets ) {
        this.assets = assets;
        this.main = main;

        hud = new Stage( new ExtendViewport( main.width*1.5f , main.height*1.5f ) );
        skin = new Skin( Gdx.files.internal( "ui/default.json" ) );

        gameOverLabel = new Label("Game\nOver", skin, "big");
        gameOverLabel.setVisible( false );

        restartButton = new TextButton( "RESTART", skin );
        restartButton.setVisible( false );
        restartButton.padLeft( 50 );
        restartButton.padRight( 50 );
        restartButton.addListener( new InputListener() {
            public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
                SoundController.getInstance().playClick();
                return true;
            };
            public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
                Controllers.getInstance().gameOver = false;
                main.setScreen( new GameScreen( main ) );
            };
        } );

        dialogLabel = new Label( "", skin );
        //Window dialogWindow = new Window( "", skin.get( "window_green", WindowStyle.class ) );
        //dialogWindow.add( dialogLabel ).growX();

        lifeLabel = new Label( "SHIP INT", skin.get( "big_32", LabelStyle.class ) );
        lifeWindow = new Window( "", skin/*.get( "window_green", WindowStyle.class )*/ );
        lifeWindow.add( lifeLabel );
        
        table = new Table();
        table.setFillParent(true);
        //table.setVisible( false );
        
        //table.add( dialogWindow ).growX();

        table.row();
        table.add().expandY();

        table.row();
        table.add( gameOverLabel );

        table.row().padTop( 100 );
        table.add( restartButton );

        table.row();
        table.add().expandY();

        table.row();
        table.add( lifeWindow );

        hud.addActor( table );

        tutorialManager = new TutorialManager();
        warningManager = new WarningManager();


        credits = new Table();
        credits.setFillParent(true);
        credits.setVisible( false );

        Label label = new Label("Thanks For Playing!", skin);
        credits.add(label);

        credits.row();
        label = new Label("Libgdx\n+\nJBUMP", skin, "big");
        label.setAlignment( Align.center );
        credits.add(label).spaceTop(40.0f).spaceBottom(40.0f);

        credits.row();
        label = new Label(
            "I would like to give thanks\n"+
            "to these people who helped\n"+
            "me during the creation\n"+
            "of this game:\n\n\n"+
            "Libgdx Community\n\n"+
            "OpenGameArt\n\n"+
            "Other Free Software\n\n"+
            "Erwin Magno (Playtest)\n\n"+
            "JGRAN\n\n"+
            "and also You!",
                skin );
        label.setAlignment( Align.center );
        credits.add(label);
        hud.addActor(credits);
    }

    private String lifeToBars( int life ) {
        String val = "X";
        switch( life ) {
            case 1:
                val = "|";
                break;
            case 2:
                val = "||";
                break;
            case 3:
                val = "|||";
                break;
            case 4:
                val = "||||";
                break;
            case 5:
                val = "|||||";
                break;
            case 6:
                val = "||||||";
                break;
            case 7:
                val = "|||||||";
                break;
            case 8:
                val = "||||||||";
                break;
            case 9:
                val = "|||||||||";
                break;
            case 10:
                val = "||||||||||";
                break;
        }
        return val;
    }

    public void update( int life ) {
        hud.getViewport().apply();
        hud.act();
        hud.draw();

        lifeLabel.setText( "SHIP INT: " + lifeToBars( life ) );

        if( !Controllers.getInstance().bossController().boss && System.currentTimeMillis() > Controllers.getInstance().bossController().timeStart ) {
            warningManager.show();
        }
    }

    public void showCredits() {
        lifeWindow.setVisible( false );
        credits.addAction( Actions.sequence(
            Actions.moveTo( credits.getX(), - table.getHeight() ),
            Actions.visible( true ),
            Actions.moveTo( credits.getX(), table.getHeight(), 30f ),
            new Action() {
                @Override
                public boolean act( float delta ) {
                    main.setScreen( new MenuScreen( main ) );
                    return true;
                }
            }
        ) );
    }

    public void gameOver() {
        //table.setVisible( true );
        gameOverLabel.setVisible( true );
        restartButton.setVisible( true );
    }

    public void showTutorial() {
        tutorialManager.show( Controllers.getInstance().tutorial );
    }

    public void showWarning() {
        warningManager.show();
    }

    public void updateSize( float width, float height ) {
        tutorialManager.updateSize( hud.getWidth(), hud.getHeight() );
        warningManager.updateSize( hud.getWidth(), hud.getHeight() );
    }

    @Override
    public void dispose() {
        hud.dispose();
        skin.dispose();
    }

    private class WarningManager {
        Window window;
        boolean showing = false;

        public WarningManager() {
            window = new Window( "", skin.get( "window_red", WindowStyle.class ) );
            window.pad( 15f );
            window.setVisible( false );
            updateSize( hud.getWidth(), hud.getHeight() );

            Label warninLabel = new Label( "BOSS", skin.get( "big", LabelStyle.class ) );
            window.add( warninLabel );

            hud.addActor( window );
        }

        public void show() {
            if( showing )
                return;
            window.setVisible( true );
            window.addAction(
                Actions.sequence( 
                    Actions.repeat( 4, Actions.sequence( Actions.visible( true ), Actions.delay( .8f ), Actions.visible( false ), Actions.delay( .2f ) ) ),
                    Actions.visible( false )
                )
            );
            SoundController.play( SoundController.getInstance().summon );
            showing = true;
        }

        public void updateSize( float width, float height ) {
            window.setModal( true );
            window.setSize( width, 200 );
            window.setPosition( 0, height/2f - window.getHeight()/2f );
        }
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
            updateSize( hud.getWidth(), hud.getHeight() );

            img = new Image();
            ship = new Image( assets.findRegion( "ship" ) );
            preview = new Image();

            title = new Label( "", skin );
            title.setAlignment( Align.center );
            message = new Label( "", skin );
            message.setAlignment( Align.center );

            TextButton closeButton = new TextButton( "GOT IT", skin );
            closeButton.addListener( new InputListener() {
                public boolean touchDown( InputEvent event, float x, float y, int pointer, int button ) {
                    SoundController.getInstance().playClick();
                    return true;
                };
                public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
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
            window.setPosition( 0, hud.getHeight()/2f - window.getHeight()/2f );
            Controllers.getInstance().tutorial = -1;
        }

        public void updateSize( float width, float height ) {
            window.setModal( true );
            window.setSize( width, 320 );
            window.setPosition( 0, height/2f - window.getHeight()/2f );
        }
    }
}
