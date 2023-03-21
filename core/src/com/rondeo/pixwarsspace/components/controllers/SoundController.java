package com.rondeo.pixwarsspace.components.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class SoundController implements Disposable {
    public Music bgm, intro, alarm, explosion;
    public Sound health, summon, clear, hurt, death, select;

    private static SoundController instance = null;

    public SoundController() {
        bgm = Gdx.audio.newMusic( Gdx.files.internal( "sound/bgm.ogg" ) );
        bgm.setLooping( true );
        alarm = Gdx.audio.newMusic( Gdx.files.internal( "sound/sfx_alarm.ogg" ) );
        //alarm.setLooping( true );
        intro = Gdx.audio.newMusic( Gdx.files.internal( "sound/intro.ogg" ) );
        explosion = Gdx.audio.newMusic( Gdx.files.internal( "sound/sfx_explosion.ogg" ) );
        

        health = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_health.ogg" ) );
        summon = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_summon.ogg" ) );
        clear = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_clear.ogg" ) );
        hurt = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_hurt.ogg" ) );
        death = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_death.ogg" ) );
        select = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_select.ogg" ) );
    }

    public static synchronized SoundController getInstance() {
        if( instance == null ) {
            instance = new SoundController();
        }
        return instance;
    }

    @Override
    public void dispose() {
        intro.dispose();
    }
}
