package com.example.hellotolivewallpaper;

import android.content.res.Configuration;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.view.IRendererListener;

import java.io.IOException;

public class HelloToLiveWallpaperService extends BaseLiveWallpaperService implements IOnSceneTouchListener
{
    private static final String TAG = "HelloTo";

    private static float CAMERA_WIDTH=1300;
    private static float CAMERA_HEIGHT=600;

    private Scene mScene;
    private TextureRegion mEyeTR;
    private Sprite mEyeSprite;
    private TextureRegion mIrisTR;
    private Sprite mIrisSprite;
    Time time;
    BitmapTextureAtlas textureAtlas;
    BitmapTextureAtlas textureAtlasIris;
    DisplayMetrics displayMetrics;
    int orient=0;
    float x,y;

    @Override
public EngineOptions onCreateEngineOptions()
{
    try
    {
        displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        wm.getDefaultDisplay().getRotation();
        CAMERA_WIDTH = displayMetrics.widthPixels;
        CAMERA_HEIGHT = displayMetrics.heightPixels;
    } catch (Exception e)
    {
        Log.e(TAG, "onCreateEngineOptions " + e.toString());
    }


    //create a camera
    Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

    EngineOptions engineOptions=new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
    engineOptions.getRenderOptions().setDithering(false);

    //engine options
    return new EngineOptions(true, ScreenOrientation.PORTRAIT_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
}
    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine(this);
    }

    public class LiveWallpaperEngine extends BaseWallpaperGLEngine {

        public LiveWallpaperEngine(IRendererListener pRendererListener) {
            super(pRendererListener);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            mEngine.onTouch(null, MotionEvent.obtain(event));
        }
    }

    @Override
    protected void onTap(int pX, int pY) {
    }
    String eye_asset_name;
    String iris_asset_name;
    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
    {
        time = new Time();
        time.setToNow();

        if(time.hour>=1 && time.hour<=6){
            eye_asset_name="gfx/blue.png";
            iris_asset_name="gfx/iris_std.png";
        }

        else if(time.hour>=7 && time.hour<=12){
            eye_asset_name="gfx/purp.png";
            iris_asset_name="gfx/iris_std.png";
        }

        else if(time.hour>=13 && time.hour<=18){
            eye_asset_name="gfx/red.png";
            iris_asset_name="gfx/iris_red.png";
        }

        else if(time.hour>=19 && time.hour<=24){
            eye_asset_name="gfx/sharingan.png";
            iris_asset_name="gfx/iris_shari.png";
        }


        //create texture
        textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1920, 1920,TextureOptions.BILINEAR);
        mEyeTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this,eye_asset_name, 0, 0);
        textureAtlas.load();

        textureAtlasIris = new BitmapTextureAtlas(this.getTextureManager(), 200, 200,TextureOptions.BILINEAR);
        mIrisTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, this,iris_asset_name, 0, 0);
        textureAtlasIris.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    float colorInF(float col){
        return col/255;
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
    {
        this.mEngine.registerUpdateHandler(new IUpdateHandler() {
            public void onUpdate(float pSecondsElapsed) {

                time.setToNow();

                String temp=eye_asset_name;

                if(time.hour>=1 && time.hour<=6){
                    eye_asset_name="gfx/blue.png";
                    iris_asset_name="gfx/iris_std.png";
                    mScene.setBackground(new Background(colorInF(25), colorInF(160), colorInF(224)));
                }

                else if(time.hour>=7 && time.hour<=12){
                    eye_asset_name="gfx/purp.png";
                    iris_asset_name="gfx/iris_std.png";
                    mScene.setBackground(new Background(colorInF(123), colorInF(31), colorInF(162)));
                }
                else if(time.hour>=13 && time.hour<=18){
                    eye_asset_name="gfx/red.png";
                    iris_asset_name="gfx/iris_red.png";
                    mScene.setBackground(new Background(colorInF(173), colorInF(32), colorInF(45)));
                }

                else if(time.hour>=19 && time.hour<=24){
                    eye_asset_name="gfx/sharingan.png";
                    iris_asset_name="gfx/iris_shari.png";
                    mScene.setBackground(new Background(0f, 0f, 0f));
                }

                if(!eye_asset_name.equals(temp)){

                if(time.hour>=1 && time.hour<=6){
                    mEyeTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(),"gfx/blue.png", 0, 0);
                    mIrisTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(),"gfx/iris_std.png", 0, 0);

                }

                else if(time.hour>=7 && time.hour<=12){
                    mEyeTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(),"gfx/purp.png", 0, 0);
                    mIrisTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(),"gfx/iris_std.png", 0, 0);
                }

                else if(time.hour>=13 && time.hour<=18){
                    mEyeTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(),"gfx/red.png", 0, 0);
                    mIrisTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(),"gfx/iris_red.png", 0, 0);
                }

                else if(time.hour>=19 && time.hour<=24){
                    mEyeTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(),"gfx/sharingan.png", 0, 0);
                    mIrisTR=BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(),"gfx/iris_shari.png", 0, 0);
                }}

            }

            public void reset() {
                // TODO Auto-generated method stub
                Log.e(TAG, "reset: " );
            }
        });
        //create a scene
        mScene = new Scene();

        //create a sprite
        mEyeSprite = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/1.5f, mEyeTR, this.getVertexBufferObjectManager());
        mScene.attachChild(mEyeSprite);


        mIrisSprite = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/1.5f, mIrisTR, this.getVertexBufferObjectManager());
        mScene.attachChild(mIrisSprite);

        //register scene touch listener
        mScene.setOnSceneTouchListener(this);

        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    @Override
public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mEyeSprite.setWidth(CAMERA_WIDTH*1.1f);
            mEyeSprite.setHeight(CAMERA_HEIGHT*2);
            mIrisSprite.setWidth(CAMERA_WIDTH*0.073f);
            mIrisSprite.setHeight(CAMERA_HEIGHT*0.13f);
            orient=1;
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mEyeSprite.setWidth(1920);
            mEyeSprite.setHeight(1920);
            mIrisSprite.setWidth(140);
            mIrisSprite.setHeight(140);
            orient=0;
        }
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
    {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {int myEventAction = pSceneTouchEvent.getAction();
        MotionEvent event = pSceneTouchEvent.getMotionEvent();
        x = event.getX();
        y = event.getY();
        if (pScene == mScene)
        {
            switch (myEventAction)
            {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(orient==0){
                    mIrisSprite.setPosition((CAMERA_WIDTH/2.25f)+x/9,CAMERA_HEIGHT-((CAMERA_HEIGHT/3.4f)+y/14));}
                    else if(orient==1){
                        mIrisSprite.setPosition((CAMERA_WIDTH/2.2f)+x/19,CAMERA_HEIGHT-((CAMERA_HEIGHT/3.8f)+y/4));}
                    break;
                case MotionEvent.ACTION_UP:
                    mIrisSprite.setPosition(CAMERA_WIDTH/2,CAMERA_HEIGHT/1.5f);
                    break;
            }
        }

        return true;
    }

}