package com.mygdx.game;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;

public class Ball {
    private Vector3 position;
    private Vector3 rotation;
    private float speed;
    private float rotateSpeed;
    private float leftRightSpeed;
    private ModelInstance ballInstance;


    // collisions
    btCollisionShape ballShape;
    btCollisionObject ballObject;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;


    private boolean startGameFallingStage;
    private boolean wasColliding;
    private Boolean isFallingDown;
    private Boolean feltDown;



    Ball(Vector3 Position, Model model)
    {
        position = Position;
        rotation = new Vector3(0.0f, 0.0f, 0.0f);

        speed = 4.f;
        rotateSpeed = 50.f;
        leftRightSpeed = 1.f;


        ballInstance = new ModelInstance(model);
        //ballInstance.transform.setToTranslation(0.0f, 0.0f, 0.0f);
        ballInstance.transform.setToTranslation(Position);
        isFallingDown=false;
        startGameFallingStage = true;

        // collisions
        ballShape = new btSphereShape(0.5f);
        ballObject = new btCollisionObject();
        ballObject.setCollisionShape(ballShape);
        ballObject.setWorldTransform(ballInstance.transform);

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);

        wasColliding = false;
        feltDown = false;
    }


    public void setWasColliding(boolean isColliding)
    {
        wasColliding = isColliding;
    }

    public boolean getWasColliding()
    {
        return wasColliding;
    }

    //public void setFeltDown(boolean isFeltDown)
    //{
       //eltDown=isFeltDown;
    //}

    public boolean getFeltDown()
    {
        return feltDown;
    }

    //public boolean getIsFallingDown()
    //{
        //return isFallingDown;
    //}

    public Vector3 getPosition()
    {
        return  position;
    }


    public void update(float accelX, float accelY, float accelZ, float elapsedTime)
    {

        // kula wyleciała poza planszę
        if(position.x <=-3.5f || position.x >=3.5f)
        {
            if(position.y>= -10.0f )
                position.y -=speed*elapsedTime;
            else
            {
                feltDown=true;
            }
        }
        else
        {
            // kula porusza się
            if(accelZ > 1.5f)
            {
                position.z += accelZ * speed * elapsedTime;
                rotation.x += accelZ * rotateSpeed * elapsedTime;
            }

            if(accelX < -2.0f)
            {
                position.x -= -accelX * leftRightSpeed * elapsedTime;
                rotation.y -= -accelX * rotateSpeed * elapsedTime;
            }

            if(accelX > 2.0f)
            {
                position.x += accelX * leftRightSpeed * elapsedTime;
                rotation.y += accelX * rotateSpeed * elapsedTime;
            }
        }
    }

    public void draw(ModelBatch modelBatch, Environment environment)
    {

        Matrix4 matrix = new Matrix4();
        matrix.rotate(Vector3.X, rotation.x);
        matrix.rotate(Vector3.Y, rotation.y);
        matrix.rotate(Vector3.Z, rotation.z);
        matrix.trn(position);

        ballInstance.transform=matrix;

        ballObject.setWorldTransform(ballInstance.transform);
        modelBatch.render(ballInstance, environment);
    }

    protected void finalize()
    {
        ballObject.dispose();
        ballShape.dispose();
        dispatcher.dispose();
        collisionConfig.dispose();
    }
}
