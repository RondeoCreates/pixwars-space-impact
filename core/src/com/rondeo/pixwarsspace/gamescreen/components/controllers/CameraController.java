package com.rondeo.pixwarsspace.gamescreen.components.controllers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.rondeo.pixwarsspace.utils.Rumble;

public class CameraController {
    OrthographicCamera camera;
    Vector2 camPos = new Vector2();

    public CameraController( OrthographicCamera camera ) {
        this.camera = camera;
    }

    public void act( Stage stage, float delta ) {
        camPos.set( MathUtils.lerp( camera.position.x, camera.viewportWidth/2, delta * 2f ), MathUtils.lerp( camera.position.y, camera.viewportHeight/2, delta * 2 ) );
        camera.position.set( camPos, 0 );

        if( Rumble.getRumbleTimeLeft() > 0 ) {
            if( camera.position.x > camera.viewportWidth/2 - 5f && camera.position.x < camera.viewportWidth/2 + 5f ) {
                Rumble.tick( delta );
                camera.translate( Rumble.getPos().x, 0 );
            }
        }
    }

}
