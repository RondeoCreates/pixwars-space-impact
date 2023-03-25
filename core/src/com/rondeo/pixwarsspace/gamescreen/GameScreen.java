package com.rondeo.pixwarsspace.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.jbump.World;
import com.rondeo.pixwarsspace.Main;
import com.rondeo.pixwarsspace.gamescreen.components.Controllers;
import com.rondeo.pixwarsspace.gamescreen.components.Entity;
import com.rondeo.pixwarsspace.gamescreen.components.HudManager;
import com.rondeo.pixwarsspace.gamescreen.components.Outbound;
import com.rondeo.pixwarsspace.gamescreen.components.entity.Ship;
import com.rondeo.pixwarsspace.utils.Background;
import com.rondeo.pixwarsspace.utils.SoundController;

public class GameScreen extends ScreenAdapter {
    //private Main main;
    private Stage stage;
    private OrthographicCamera camera;
    private InputMultiplexer inputMultiplexer;

    private TextureAtlas assets;
    private World<Entity> world;
    private Outbound[] outbound = new Outbound[4];
    private Background background;
    private Ship ship;

    private HudManager hudManager;

    public GameScreen( Main main ) {
        //this.main = main;
        stage = new Stage( new FitViewport( main.width, main.height, camera = new OrthographicCamera() ) );
        assets = new TextureAtlas( Gdx.files.internal( "assets.atlas" ) );
        
        world = new World<Entity>( 4f );
        //world.setTileMode( false );

        ship = new Ship( world );
        ship.setRegions( assets.findRegion( "ship" ), assets.findRegion( "wing" ), assets.findRegion( "ship_sketch" ), assets.findRegion( "wing_sketch" ), assets.findRegions( "thrusters" ), assets.findRegion( "effect" ) );
        background = new Background( main.width, main.height, ship );

        Controllers.getInstance().init( world, camera, assets, ship );
        outbound[0] = new Outbound( world, 0, main.height + 1, main.width, 10 );
        outbound[1] = new Outbound( world, 0, -11, main.width, 10 );
        outbound[2] = new Outbound( world, -11, 0, 10, main.height );
        outbound[3] = new Outbound( world, main.width + 1, 0, 10, main.height );

        stage.addActor( background );
        stage.addActor( ship );
        stage.addActor( Controllers.getInstance().enemyController() );
        stage.addActor( Controllers.getInstance().bossController() );
        Controllers.getInstance().bossController().setup();
        
        hudManager = new HudManager( main, assets );

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor( ship );
        inputMultiplexer.addProcessor( stage );
        inputMultiplexer.addProcessor( hudManager.hud );
        Gdx.input.setInputProcessor( inputMultiplexer );
    }

    @Override
    public void render( float delta ) {
        super.render( delta );

        stage.getViewport().apply();
        ScreenUtils.clear( Color.valueOf( "#2f033b" ) );
        
        if( Controllers.getInstance().tutorial >= 0 ) {
            hudManager.showTutorial();
            Controllers.getInstance().pause = true;
        }
        if( Controllers.getInstance().credits == 1 ) {
            hudManager.showCredits();
            Controllers.getInstance().credits = 2;
        }
        if( !Controllers.getInstance().pause ) {
            if( !Controllers.getInstance().gameOver ) {
                stage.act();
            } else {
                stage.act( .0003f );
                hudManager.gameOver();
            }
        }
        stage.draw();

        hudManager.update( ship.life );

        Controllers.getInstance().act( stage, delta );

        if( Gdx.input.isKeyJustPressed( Keys.P ) ) {
            Controllers.getInstance().pause = !Controllers.getInstance().pause;
        }

        if( Gdx.input.isKeyPressed( Keys.L ) ) {
            System.out.println( world.countItems() + "<>" + world.countCells() + " = " + Gdx.graphics.getFramesPerSecond() );
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        stage.getViewport().update( width, height, true );
        hudManager.hud.getViewport().update( width, height, true );
        hudManager.updateSize( width, height );
        outbound[0].update( 0, height + 1, width, 10 );
        outbound[1].update( 0, -11, width, 10 );
        outbound[2].update( -11, 0, 10, height );
        outbound[3].update( width + 1, 0, 10,height );
        //background.setBounds( 0, 0, width, height );
    }


    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        background.dispose();
        ship.dispose();
        Controllers.getInstance().dispose();
        assets.dispose();
        hudManager.dispose();
        SoundController.getInstance().dispose();
    }

}
