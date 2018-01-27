package com.pupilla.dpk.Sprites;

import com.badlogic.gdx.physics.box2d.World;
import com.pupilla.dpk.Backend.Constants;
import com.pupilla.dpk.Backend.Item;

import java.util.ArrayList;

/**
 * Created by orzech on 27.01.2018.
 */

public class Seller extends NPC{

    private static final String TAG = "Seller";
    public ArrayList<Item> items = new ArrayList<Item>();

    public Seller(String dialoguePath, World world, int level) {
        super(dialoguePath, world);
        setProducts(level);
    }

    private void setProducts(int level){
        switch(level){
            case 0:case 1:default:
                items.add(new Item(Constants.eqSteelSword, 1, Item.Type.weapon));
                items.add(new Item(Constants.eqWoodenShield, 1, Item.Type.shield));
                break;
        }
    }
}
