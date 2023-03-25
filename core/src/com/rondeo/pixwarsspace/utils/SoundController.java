package com.rondeo.pixwarsspace.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class SoundController implements Disposable {
    public Music bgm, intro, alarm, explosion;
    public Sound health, summon, clear, hurt, death, select;
    public float sfxVol = 1;

    private static SoundController instance = null;
    

    public SoundController() {
        bgm = Gdx.audio.newMusic( Gdx.files.internal( "sound/bgm.ogg" ) );
        bgm.setLooping( true );
        alarm = Gdx.audio.newMusic( Gdx.files.internal( "sound/sfx_alarm.ogg" ) );
        intro = Gdx.audio.newMusic( Gdx.files.internal( "sound/intro.ogg" ) );
        explosion = Gdx.audio.newMusic( Gdx.files.internal( "sound/sfx_explosion.ogg" ) );
        

        health = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_health.ogg" ) );
        summon = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_summon.ogg" ) );
        clear = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_clear.ogg" ) );
        hurt = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_hurt.ogg" ) );
        death = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_death.ogg" ) );
        select = Gdx.audio.newSound( Gdx.files.internal( "sound/sfx_select.ogg" ) );
    }

    public void initVolume( Preferences preferences ) {
        if( !preferences.contains( "bgm_volume" ) ) {
            preferences.putFloat( "bgm_volume", .1f );
            preferences.flush();
        }
        if( !preferences.contains( "sfx_volume" ) ) {
            preferences.putFloat( "sfx_volume", .5f );
            preferences.flush();
        }

        float bgmVol = preferences.getFloat( "bgm_volume" );
        bgm.setVolume( bgmVol );
        alarm.setVolume( bgmVol );
        intro.setVolume( bgmVol );
        explosion.setVolume( bgmVol );
        
        sfxVol = preferences.getFloat( "sfx_volume" );
    }

    public void playClick() {
        hurt.play( sfxVol );
    }

    public void setVolume( float bgm, float sfx, Preferences preferences ) {
        if( !preferences.contains( "bgm_volume" ) ) {
            preferences.putFloat( "bgm", bgm );
            preferences.flush();
        }
        if( !preferences.contains( "sfx_volume" ) ) {
            preferences.putFloat( "sfx_volume", sfx );
            preferences.flush();
        }
        initVolume( preferences );
    }

    public static void play( Sound sound ) {
        sound.play( getInstance().sfxVol );
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
