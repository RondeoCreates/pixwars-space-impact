package com.rondeo.pixwarsspace;

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
import com.rondeo.pixwarsspace.components.Background;
import com.rondeo.pixwarsspace.components.Controllers;
import com.rondeo.pixwarsspace.components.Entity;
import com.rondeo.pixwarsspace.components.HudManager;
import com.rondeo.pixwarsspace.components.Outbound;
import com.rondeo.pixwarsspace.components.controllers.BossController;
import com.rondeo.pixwarsspace.components.controllers.SoundController;
import com.rondeo.pixwarsspace.components.entity.Ship;

public class GameScreen extends ScreenAdapter {
    Main main;
    Stage stage;
    OrthographicCamera camera;
    InputMultiplexer inputMultiplexer;

    TextureAtlas assets;
    World<Entity> world;
    Outbound[] outbound = new Outbound[4];
    Background background;
    Ship ship;

    HudManager hudManager;

    public GameScreen( Main main ) {
        this.main = main;
        stage = new Stage( new FitViewport( main.width, main.height, camera = new OrthographicCamera() ) );
        assets = new TextureAtlas( Gdx.files.internal( "assets.atlas" ) );
        
        world = new World<>( 1f );
        world.setTileMode( false );

        ship = new Ship( world );
        ship.setRegions( assets.findRegion( "ship" ), assets.findRegion( "wing" ), assets.findRegion( "ship_sketch" ), assets.findRegion( "wing_sketch" ), assets.findRegions( "thrusters" ), assets.findRegion( "effect" ) );
        background = new Background( main.width, main.height, ship, assets );

        Controllers.getInstance().init( world, camera, assets, ship );
        outbound[0] = new Outbound( world, 0, main.height + 1, main.width, 10 );
        outbound[1] = new Outbound( world, 0, -11, main.width, 10 );
        outbound[2] = new Outbound( world, -11, 0, 10, main.height );
        outbound[3] = new Outbound( world, main.width + 1, 0, 10, main.height );

        stage.addActor( background );
        stage.addActor( ship );
        stage.addActor( Controllers.getInstance().enemyController );

        BossController bossController = new BossController( world, assets.findRegions( "tentacles" ), assets.findRegion( "head" ), assets.findRegion( "hand" ) );
        stage.addActor( bossController );
        bossController.setup();
        
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
        if( !Controllers.getInstance().pause ) {
            if( !Controllers.getInstance().gameOver ) {
                stage.act();
            } else if( Gdx.input.isTouched( 1 ) ) {
                Controllers.getInstance().gameOver = false;
                main.setScreen( new GameScreen( main ) );
            } else {
                stage.act( .0003f );
                hudManager.gameOver();
            }
        }
        stage.draw();

        hudManager.update();

        Controllers.getInstance().act( stage, delta );

        if( Gdx.input.isKeyJustPressed( Keys.P ) ) {
            Controllers.getInstance().pause = !Controllers.getInstance().pause;
            System.out.println( Controllers.getInstance().pause );
            System.out.println( world.countItems() + "<>" + world.countCells() );
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        
        stage.getViewport().update( width, height, true );
        hudManager.hud.getViewport().update( width, height, true );
        outbound[0].update( 0, height + 1, width, 10 );
        outbound[1].update( 0, -11, width, 10 );
        outbound[2].update( -11, 0, 10, height );
        outbound[3].update( width + 1, 0, 10,height );
        background.setBounds( 0, 0, width, height );
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
