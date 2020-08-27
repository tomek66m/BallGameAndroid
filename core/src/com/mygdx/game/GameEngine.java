package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.mygdx.game.Ball;
import com.mygdx.game.Box;
import com.mygdx.game.GameState;
import com.mygdx.game.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.jws.WebParam;

public class GameEngine {

    // kamera
    private PerspectiveCamera mCamera;
    private ModelBatch mModelBatch;
    // obiekt do budowania modeli
    private ModelBuilder mModelBuilder;
    // środowisko do generowania światła
    private Environment mEnvironment;
    private SpriteBatch mBatch;


    // wartości z akcelereometru
    private float mAccelX;
    private float mAccelY;
    private float mAccelZ;

    // informacje o renderingu
    private float mElapsedTime; // czas od ostatniej klatki


    private long mStartTime; // odliczanie czasu po upadku kuli

    private AssetManager mAssetManager; // menadżer assetów

    // obiekty w grze
    private Ball mBall;
    //private Path mPath;
    //private Box mBox;
    private List<Path> mPaths;
    private List<Box> mBoxes;

    static Vector3 mInitalBallPosition = new Vector3(0.0f, 0.9f, -1.0f);

    // spriety
    Texture mBackground;
    Sprite mBackgroundSprite;

    // sciezki do assetow
    private String mBallModelPath, mPathModelPath, mBoxModelPath, mSkyboxPicturePath, mFontPath;

    private BitmapFont mFont; // obiekt zawierający czcionkę

    private GameState mGameState; // status gry
    private int mScore; // wynik gry
    private int mHighScore;

    private long mTimeThatBallFelt;

    GameEngine(float camPositionX, float camPositionY, float camPositionZ, float camLookAtX, float camLookAtY, float camLookAtZ, float camNear, float camFar)
    {
        // setting the camera
        mCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        mCamera.position.set(0.0f, 0.0f, 10.0f); // ruszamy tylko X i Z przy podążaniu za kulą
        mCamera.lookAt(0.0f,0.0f,10.0f);
        mCamera.near = 1.0f;
        mCamera.far = 300.0f;
        mCamera.update();

        mModelBatch = new ModelBatch();
        mModelBuilder = new ModelBuilder();

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight,0.8f,0.8f,0.8f,1.0f));

        mBatch = new SpriteBatch();

        mAssetManager = new AssetManager();


        Bullet.init();

        mGameState = GameState.LOADING;
        mScore = 0;


    }

    // getters and setters

    public GameState getmGameState()
    {
        return mGameState;
    }
    public void setmGameState(GameState gameState)
    {
        mGameState = gameState;
    }

    public AssetManager getmAssetManager() {
        return mAssetManager;
    }

    public void loadAssets(String ballModelPath, String pathModelPath, String boxModelPath, String skyboxPicturePath, String fontPath)
    {
        mBallModelPath = ballModelPath;
        mPathModelPath = pathModelPath;
        mBoxModelPath = boxModelPath;
        mSkyboxPicturePath = skyboxPicturePath;
        mFontPath = fontPath;


        mAssetManager.load(mBallModelPath, Model.class);
        mAssetManager.load(mPathModelPath, Model.class);
        mAssetManager.load(mBoxModelPath, Model.class);

        mAssetManager.load(mSkyboxPicturePath, Texture.class);
    }

    public void initObjects(Vector3 firstPathPosition, int howMuchPathsToGenerate, Vector3 firstBoxPosition, int howMuchBoxesToGenerate )
    {
        Model ballModel, pathModel, boxModel;
        ballModel = mAssetManager.get(mBallModelPath, Model.class);
        pathModel = mAssetManager.get(mPathModelPath, Model.class);
        boxModel = mAssetManager.get(mBoxModelPath, Model.class);


        // GameEngine.mInitalBallPosition = ballPosition;
        mBall = new Ball(new Vector3(mInitalBallPosition), ballModel);

        // generowanie ścieżki
        mPaths = new ArrayList<Path>();

        for(int i=0;i<howMuchPathsToGenerate;i++)
        {
            Path temp = new Path(new Vector3(
                    firstPathPosition.x,
                    firstPathPosition.y,
                    firstPathPosition.z + i*30.0f
                    ),
                    pathModel);
            mPaths.add(temp);
        }

        mBoxes = new ArrayList<Box>();

        // losowanie pozycji skrzynek
        Random generator = new Random();
        for(int i=0;i<howMuchBoxesToGenerate;i++)
        {
            float d = generator.nextFloat() * (2.5f + 2.5f) -2.5f;
            float g = generator.nextFloat() * (5.0f - 0.0f) +0.0f;
            Box temp = new Box(new Vector3(
                    firstBoxPosition.x + d,
                    firstBoxPosition.y + 1.0f,
                    firstBoxPosition.z +(i*10.0f+g)
                    ),
                    boxModel);
            mBoxes.add(temp);
        }

        // ładowanie skyboxa
        mBackground = new Texture(Gdx.files.internal(mSkyboxPicturePath)); //chyba nie trzeba
        //mBackground.load(mAssetManager.get(mSkyboxPicturePath, Texture.class).getTextureData());
        mBackgroundSprite = new Sprite(mBackground);

        mFont = new BitmapFont(Gdx.files.internal(mFontPath), false);

        //mGameState = GameState.PLAYING;

    }

    public void updateForPlaying()
    {
        // pobranie wartości z akcelerometra
        mAccelX = Gdx.input.getAccelerometerX();
        mAccelY = Gdx.input.getAccelerometerY();
        mAccelZ = Gdx.input.getAccelerometerZ();

        // aktualizacja pozycji piłki

        mBall.update(mAccelX, mAccelY, mAccelZ, Gdx.graphics.getDeltaTime());

        // sprawdzenie czy kula wypadła ze ścieżki
        if(mBall.getFeltDown()==true)
        {
            mGameState=GameState.GAMEOVER;
            mStartTime = System.currentTimeMillis();
        }

        // sprawdzenie kolizji kuli ze skrzynkami
        for(int i=0;i<mBoxes.size();i++)
        {
            if(CollisionDetector.checkCollision(mBall.ballObject, mBoxes.get(i).boxObject, mBall.dispatcher) == true)
                mBall.setWasColliding(true);
        }

        if(mBall.getWasColliding() == true)
        {
            mGameState = GameState.GAMEOVER;
            mTimeThatBallFelt = System.currentTimeMillis();
        }
        else
        {
            mHighScore = mScore;
            mScore = 0;
            for(int i=0;i<mBoxes.size();i++)
            {
                if(mBall.getPosition().z > mBoxes.get(i).getPosition().z)
                {
                    mBoxes.get(i).wasOmitted=true;
                }
                if(mBoxes.get(i).wasOmitted==true)
                {
                    mScore++;
                }

            }
        }

        // wersja demo

        //if(mBall.getPosition().z >= 100.0f)
            //mG


        // aktualizacja pozycji kamery TODO
        mCamera.position.set(mBall.getPosition().x ,
                (mBall.getPosition().y +1.0f) ,
                (mBall.getPosition().z -5.0f) );
        mCamera.lookAt(mBall.getPosition());
        mCamera.update();
    }

    public void updateForGameOver()
    {

        mBall.update(mAccelX, mAccelY, mAccelZ, mElapsedTime);

        // liczenie 3 sekund odkąd kula upadła
        long timePassed = System.currentTimeMillis() - mTimeThatBallFelt / 1000;


        if(Gdx.input.isTouched() && timePassed >= 3.0f)
        {

            //mBall.finalize();
            Model ballModel = mAssetManager.get(mBallModelPath, Model.class);
            mBall = new Ball(new Vector3(GameEngine.mInitalBallPosition), ballModel);

            for (Box temp :
                    mBoxes) {
                temp.wasOmitted=false;
            }

            mScore = 0;

            mGameState = GameState.PLAYING;

        }
    }

    public void draw()
    {
        if(mGameState == GameState.PLAYING)
        {
            Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

            // rysowanie skyboxa
            mBatch.begin();
            mBatch.draw(mBackgroundSprite, 0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            mBatch.end();

            // rysowanie 3D

            mModelBatch.begin(mCamera);

            // rysowanie ścieżek
            for (Path x:
                    mPaths) {
                x.draw(mModelBatch, mEnvironment);
            }

            // rysowanie skrzynek
            for (Box x:
                    mBoxes){
                x.draw(mModelBatch, mEnvironment);
            }

            // rysowanie kuli
            mBall.draw(mModelBatch, mEnvironment);

            // koniec rysowania 3D
            mModelBatch.end();

            // rysowanie informacji na ekranie
            mBatch.begin();
            DebugInformer.drawInfo(mFont, mBatch,
                    "Score: " + String.valueOf(mScore),
                    "Fps: " + String.valueOf(Gdx.graphics.getFramesPerSecond()
                    ));
            mBatch.end();
        }
        if(mGameState == GameState.GAMEOVER)
        {
            Gdx.gl.glViewport(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT|GL20.GL_DEPTH_BUFFER_BIT);

            // rysowanie 3D

            mModelBatch.begin(mCamera);

            // rysowanie ścieżki
            for (Path temp :
                    mPaths) {
                temp.draw(mModelBatch, mEnvironment);
            }

            // rysowanie kuli

            mBall.draw(mModelBatch, mEnvironment);

            mModelBatch.end();

            // jeżeli minęły 3 sekundy - rysowanie menu kontynuacji
            long timePassed = System.currentTimeMillis() - mTimeThatBallFelt /1000;
            if(timePassed >=3.0f)
            {
                mBatch.begin();
                mBatch.draw(mBackgroundSprite, 0.0f, 0.0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                mFont.getData().setScale(2.0f, 2.0f);
                mFont.setColor(Color.WHITE);
                GlyphLayout layout = new GlyphLayout();
                layout.setText(mFont, "Tap to play again");
                mFont.draw(mBatch, "Tap to play again", Gdx.graphics.getWidth()/2 - layout.width/2, Gdx.graphics.getHeight()/3);
                DebugInformer.drawInfo(mFont, mBatch,
                        "MaxScore: " + String.valueOf(mHighScore));
                mBatch.end();
            }

        }
    }

    @Override
    public void finalize()
    {
        try{
            mModelBatch.dispose();
            mAssetManager.dispose();
        }
        catch (Exception ex)
        {

        }
    }


}
