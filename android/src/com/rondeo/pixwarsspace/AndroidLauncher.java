package com.rondeo.pixwarsspace;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.rondeo.pixwarsspace.Main;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true; // Recommended, but not required.
        initialize( new Main(), config );
    }
}
