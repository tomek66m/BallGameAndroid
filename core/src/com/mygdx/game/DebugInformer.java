package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class DebugInformer {


    public static void drawInfo(BitmapFont font, SpriteBatch modelBatch, String ... information)
    {
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.getData().setScale(2.0f);


        float width = Gdx.graphics.getWidth();
        float height= Gdx.graphics.getHeight();

        //int onePart = (int)(height/information.length);

        int onePart=100;


        for(int i=0;i<information.length;i++) {
            font.draw(modelBatch, information[i], 10, height-20 - onePart*i);
        }
    }

}
