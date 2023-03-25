package com.rondeo.pixwarsspace.gamescreen.components;

import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class Outbound implements Entity {
    Item<Entity> item;
    World<Entity> world;

    public Outbound( World<Entity> world, float x, float y, float width, float height ) {
        this.world = world;
        item = new Item<Entity>( this );
        world.add( item, x, y, width, height );
    }

    public void update( float x, float y, float width, float height ) {
        world.update( item, x, y, width, height );
    }

}
