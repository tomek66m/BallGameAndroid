package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
public class Path {
    private Vector3 position;
    private Vector3 size;
    private ModelInstance pathInstance;
    btCollisionShape pathShape;
    btCollisionObject pathObject;

    Path(Vector3 pos, Model model) {

        // MODEL
        position = pos;
        pathInstance = new ModelInstance(model);
        pathInstance.transform.setToTranslation(pos);

        size = new Vector3();

        pathShape = new btBoxShape(new Vector3(3.0f, 2.0f, 28.0f));
        pathObject = new btCollisionObject();
        pathObject.setCollisionShape(pathShape);
        pathObject.setWorldTransform(pathInstance.transform);

    }

    public btCollisionObject getPathObject()
    {
        return pathObject;
    }

    public Vector3 getPosition()
    {
        return  position;
    }

    public ModelInstance getModelInstance()
    {
        return pathInstance;
    }

    public void draw(ModelBatch modelBatch, Environment environment)
    {
        pathObject.setWorldTransform(pathInstance.transform);
        modelBatch.render(pathInstance, environment);
    }

    protected void finalize()
    {

    }

}
