package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;


public class Box {
    private Vector3 position;
    private Vector3 size;
    private ModelInstance boxInstance;

    // collisions
    // collisions
    btCollisionShape boxShape;
    btCollisionObject boxObject;

    boolean wasOmitted;


    Box(Vector3 pos, Model model)
    {
        // MODEL
        position = pos;
        boxInstance = new ModelInstance(model);
        boxInstance.transform.setToTranslation(pos);

        size = new Vector3();

         // collisions
        boxShape = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f));
        boxObject = new btCollisionObject();
        boxObject.setCollisionShape(boxShape);
        boxObject.setWorldTransform(boxInstance.transform);

        wasOmitted = false;
    }

    public Vector3 getPosition()
    {
        return position;
    }

    public Vector3 getCenter()
    {
        Vector3 temp = new Vector3();
        temp=boxInstance.transform.getTranslation(temp);
        return temp;
    }

    public void update()
    {

    }

    public void draw(ModelBatch modelBatch, Environment environment)
    {
        modelBatch.render(boxInstance, environment);
    }

    // finally!
    protected void finalize()
    {
        boxObject.dispose();
        boxShape.dispose();
    }
}
