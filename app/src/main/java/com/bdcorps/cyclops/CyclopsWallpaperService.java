package com.bdcorps.cyclops;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
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

public class CyclopsWallpaperService extends BaseLiveWallpaperService implements IOnSceneTouchListener {
    private static final String TAG = "LogTag";
    public static boolean isManualSet = false;
    static String eye_asset_name = "gfx/blue.png";
    static String iris_asset_name = "gfx/iris_std.png";
    private static float CAMERA_WIDTH = 1080;
    private static float CAMERA_HEIGHT = 1920;
    public SharedPreferences mPrefs = null;
    PreferenceChangeListener mPreferenceListener;
    Camera mCamera;
    Time time;
    BitmapTextureAtlas textureAtlas;
    BitmapTextureAtlas textureAtlasIris;
    DisplayMetrics displayMetrics;
    int orient;
    float x, y;
    float w, h;
    Display display;
    private Scene mScene;
    private TextureRegion mEyeTexture;
    private Sprite mEyeSprite;
    private TextureRegion mIrisTexture;
    private Sprite mIrisSprite;

    static public void setAssetNameWithPref(String design_pref) {

        switch (design_pref.toLowerCase()) {
            case "blue":
                eye_asset_name = "gfx/blue.png";
                iris_asset_name = "gfx/iris_std.png";
                break;
            case "orange":
                eye_asset_name = "gfx/orange.png";
                iris_asset_name = "gfx/iris_std.png";
                break;
            case "green":
                eye_asset_name = "gfx/green.png";
                iris_asset_name = "gfx/iris_std.png";
                break;
            case "purple":
                eye_asset_name = "gfx/purple.png";
                iris_asset_name = "gfx/iris_std.png";
                break;
            case "red":
                eye_asset_name = "gfx/red.png";
                iris_asset_name = "gfx/iris_red.png";
                break;
            case "sharingan":
                eye_asset_name = "gfx/sharingan.png";
                iris_asset_name = "gfx/iris_shari.png";
                break;
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void loadOrientation() {
        displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        wm.getDefaultDisplay().getRotation();
        w = displayMetrics.widthPixels;
        h = displayMetrics.heightPixels + getnav();
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        try {
            time = new Time();
            //Set Preference Change Listener
            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            mPreferenceListener = new PreferenceChangeListener();
            mPrefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
        } catch (Exception e) {
            Log.e(TAG, "onCreateEngineOptions " + e.toString());
        }
        ZoomCamera zoomCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(
                true,
                ScreenOrientation.PORTRAIT_SENSOR,
                new RatioResolutionPolicy(w, h),
                zoomCamera);

        zoomCamera.setResizeOnSurfaceSizeChanged(true);
        engineOptions.getRenderOptions().setDithering(true);
        return engineOptions;
    }

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine(this);
    }

    int getnav() {
        Resources resources = getBaseContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        boolean hasMenuKey = ViewConfiguration.get(getBaseContext()).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return 0;
    }

    @Override
    protected void onTap(int pX, int pY) {
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String design_pref = sharedPreferences.getString("eye_design_pref", "Automatic");

        if (design_pref.equals("Automatic")) {

            time.setToNow();

            if (time.hour >= 1 && time.hour <= 4) {
                eye_asset_name = "gfx/blue.png";
                iris_asset_name = "gfx/iris_std.png";
            } else if (time.hour >= 5 && time.hour <= 8) {
                eye_asset_name = "gfx/orange.png";
                iris_asset_name = "gfx/iris_std.png";
            } else if (time.hour >= 9 && time.hour <= 12) {
                eye_asset_name = "gfx/green.png";
                iris_asset_name = "gfx/iris_std.png";
            } else if (time.hour >= 13 && time.hour <= 16) {
                eye_asset_name = "gfx/purple.png";
                iris_asset_name = "gfx/iris_std.png";
            } else if (time.hour >= 17 && time.hour <= 20) {
                eye_asset_name = "gfx/red.png";
                iris_asset_name = "gfx/iris_red.png";
            } else if (time.hour >= 21 && time.hour <= 24) {
                eye_asset_name = "gfx/sharingan.png";
                iris_asset_name = "gfx/iris_shari.png";
            }
        } else {
            switch (design_pref.toLowerCase()) {
                case "blue":
                    eye_asset_name = "gfx/blue.png";
                    iris_asset_name = "gfx/iris_std.png";
                    break;
                case "yellow":
                    eye_asset_name = "gfx/orange.png";
                    iris_asset_name = "gfx/iris_std.png";
                    break;
                case "mike":
                    eye_asset_name = "gfx/green.png";
                    iris_asset_name = "gfx/iris_std.png";
                    break;
                case "purple":
                    eye_asset_name = "gfx/purple.png";
                    iris_asset_name = "gfx/iris_std.png";
                    break;
                case "red":
                    eye_asset_name = "gfx/red.png";
                    iris_asset_name = "gfx/iris_red.png";
                    break;
                case "sharingan":
                    eye_asset_name = "gfx/sharingan.png";
                    iris_asset_name = "gfx/iris_shari.png";
                    break;
            }
        }
        //create texture
        textureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1920, 1920, TextureOptions.BILINEAR);
        mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, this, eye_asset_name, 0, 0);
        textureAtlas.load();

        textureAtlasIris = new BitmapTextureAtlas(this.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
        mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, this, iris_asset_name, 0, 0);
        textureAtlasIris.load();


        loadOrientation();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    float colorInF(float col) {
        return col / 255;
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
        this.mEngine.registerUpdateHandler(new IUpdateHandler() {
            public void onUpdate(float pSecondsElapsed) {

                time.setToNow();

                String temp = eye_asset_name;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String design_pref = sharedPreferences.getString("eye_design_pref", "Automatic");

                if (design_pref.equals("Automatic")) {

                    setAssetNameWithTime();
                    setBackgroundWithTime();

                    if (isTablet(getBaseContext())) {
                        mEyeSprite.setSize(960, 960);
                        mIrisSprite.setSize(75, 75);
                    }

                    if (!eye_asset_name.equals(temp)) {
                        setAssetWithTime();

                    }
                } else if (!design_pref.equals("Automatic")) {
                    if (!isManualSet) {

                        setAssetNameWithPref(design_pref);
                        setAssetWithPref(design_pref);
                        setBackgroundWithPref(design_pref);

                        isManualSet = true;

                    }
                }
            }

            public void reset() {
                Log.e(TAG, "reset: ");
            }
        });
        //create a scene
        mScene = new Scene();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String pref_position = sharedPreferences.getString("positionListKey", "2");

        //create a sprite
        if (pref_position.equals("1")) {
            mEyeSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2f, mEyeTexture, this.getVertexBufferObjectManager());
            mEyeSprite.setCullingEnabled(true);
            mScene.attachChild(mEyeSprite);


            mIrisSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2f, mIrisTexture, this.getVertexBufferObjectManager());
            mIrisSprite.setCullingEnabled(true);
            mScene.attachChild(mIrisSprite);
        }

        //create a sprite
        else if (pref_position.equals("2")) {
            mEyeSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 1.5f, mEyeTexture, this.getVertexBufferObjectManager());
            mEyeSprite.setCullingEnabled(true);
            mScene.attachChild(mEyeSprite);


            mIrisSprite = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 1.5f, mIrisTexture, this.getVertexBufferObjectManager());
            mIrisSprite.setCullingEnabled(true);
            mScene.attachChild(mIrisSprite);
        }

        //register scene touch listener
        mScene.setOnSceneTouchListener(this);

        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    public void setAssetNameWithTime() {

        time.setToNow();

        if (time.hour >= 1 && time.hour <= 4) {
            eye_asset_name = "gfx/blue.png";
            iris_asset_name = "gfx/iris_std.png";
        } else if (time.hour >= 5 && time.hour <= 8) {
            eye_asset_name = "gfx/orange.png";
            iris_asset_name = "gfx/iris_std.png";
        } else if (time.hour >= 9 && time.hour <= 12) {
            eye_asset_name = "gfx/green.png";
            iris_asset_name = "gfx/iris_std.png";
        } else if (time.hour >= 13 && time.hour <= 16) {
            eye_asset_name = "gfx/purple.png";
            iris_asset_name = "gfx/iris_std.png";
        } else if (time.hour >= 17 && time.hour <= 20) {
            eye_asset_name = "gfx/red.png";
            iris_asset_name = "gfx/iris_red.png";
        } else if (time.hour >= 21 && time.hour <= 24) {
            eye_asset_name = "gfx/sharingan.png";
            iris_asset_name = "gfx/iris_shari.png";
        }
    }

    public void setBackgroundWithTime() {

        time.setToNow();

        if (time.hour >= 1 && time.hour <= 4) {
            mScene.setBackground(new Background(colorInF(25), colorInF(160), colorInF(224)));
        } else if (time.hour >= 5 && time.hour <= 8) {
            mScene.setBackground(new Background(colorInF(254), colorInF(148), colorInF(62)));
        } else if (time.hour >= 9 && time.hour <= 12) {
            mScene.setBackground(new Background(colorInF(31), colorInF(164), colorInF(99)));
        } else if (time.hour >= 13 && time.hour <= 16) {
            mScene.setBackground(new Background(colorInF(123), colorInF(31), colorInF(162)));
        } else if (time.hour >= 17 && time.hour <= 20) {
            mScene.setBackground(new Background(colorInF(173), colorInF(32), colorInF(45)));
        } else if (time.hour >= 21 && time.hour <= 24) {
            mScene.setBackground(new Background(0f, 0f, 0f));
        }
    }

    public void setBackgroundWithPref(String design_pref) {

        switch (design_pref.toLowerCase()) {
            case "blue":
                mScene.setBackground(new Background(colorInF(25), colorInF(160), colorInF(224)));
                break;
            case "orange":
                mScene.setBackground(new Background(colorInF(254), colorInF(148), colorInF(62)));
                break;
            case "green":
                mScene.setBackground(new Background(colorInF(31), colorInF(164), colorInF(99)));
                break;
            case "purple":
                mScene.setBackground(new Background(colorInF(123), colorInF(31), colorInF(162)));
                break;
            case "red":
                mScene.setBackground(new Background(colorInF(173), colorInF(32), colorInF(45)));
                break;
            case "sharingan":
                mScene.setBackground(new Background(0f, 0f, 0f));
                break;
        }
    }

    public void setAssetWithTime() {

        time.setToNow();

        if (time.hour >= 1 && time.hour <= 4) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/blue.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_std.png", 0, 0);
        } else if (time.hour >= 5 && time.hour <= 8) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/orange.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_std.png", 0, 0);
        } else if (time.hour >= 9 && time.hour <= 12) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/green.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_std.png", 0, 0);
        } else if (time.hour >= 13 && time.hour <= 16) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/purple.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_std.png", 0, 0);
        } else if (time.hour >= 17 && time.hour <= 20) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/red.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_red.png", 0, 0);
        } else if (time.hour >= 21 && time.hour <= 24) {
            mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/sharingan.png", 0, 0);
            mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_shari.png", 0, 0);
        }
    }

    public void setAssetWithPref(String design_pref) {

        mEyeTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlas, getAssets(), "gfx/" + design_pref.toLowerCase() + ".png", 0, 0);

        switch (design_pref.toLowerCase()) {
            case "blue":
            case "purple":
            case "orange":
            case "green":
                mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_std.png", 0, 0);
                break;
            case "red":
                mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_red.png", 0, 0);
                break;
            case "sharingan":
                mIrisTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(textureAtlasIris, getAssets(), "gfx/iris_shari.png", 0, 0);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float ratio = CAMERA_WIDTH / CAMERA_HEIGHT;

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orient=0;
        }

        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orient=1;
        }
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String pref_position = sharedPreferences.getString("positionListKey", "2");

        int myEventAction = pSceneTouchEvent.getAction();
        MotionEvent event = pSceneTouchEvent.getMotionEvent();
        x = event.getX();
        y = event.getY();
        if (pScene == mScene) {
            switch (myEventAction) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (pref_position.equals("1")) {
                        if (orient == 0) {
                            mIrisSprite.setPosition(((CAMERA_WIDTH / 2) + x / 8) - (mIrisSprite.getHeight()) / 2, (CAMERA_HEIGHT - y / 14) - CAMERA_HEIGHT / 2 + (mIrisSprite.getHeight()) / 2);
                        } else if (orient == 1) {
                            mIrisSprite.setPosition(((CAMERA_WIDTH / 2) + x / 14) - (mIrisSprite.getHeight()) / 2, (CAMERA_HEIGHT - y / 8) - CAMERA_HEIGHT / 2 + (mIrisSprite.getHeight()) / 2);
                        }
                    } else if (pref_position.equals("2")) {
                        if (orient == 0) {
                            mIrisSprite.setPosition(((CAMERA_WIDTH / 2) + x / 8) - (mIrisSprite.getHeight()) / 2, (CAMERA_HEIGHT - y / 14) - CAMERA_HEIGHT / 3f + (mIrisSprite.getHeight()) / 2);
                        } else if (orient == 1) {
                            mIrisSprite.setPosition(((CAMERA_WIDTH / 2) + x / 14) - (mIrisSprite.getHeight()) / 2, (CAMERA_HEIGHT - y / 8) - CAMERA_HEIGHT / 3f + (mIrisSprite.getHeight()) / 2);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (pref_position.equals("1")) {
                        mIrisSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2);
                    }
                    if (pref_position.equals("2")) {
                        mIrisSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 1.5f);
                    }
                    break;
            }
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

//        mCamera.reset();

        isManualSet = false;
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mPreferenceListener);
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

    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String pref_position = sharedPreferences.getString("positionListKey", "no selection");
            String design_pref = sharedPreferences.getString("eye_design_pref", "Automatic");

            if (key.equals("positionListKey")) {

                if (pref_position.equals("1")) {
                    mEyeSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2f);
                    mIrisSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2f);
                } else if (pref_position.equals("2")) {
                    mEyeSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 1.5f);
                    mIrisSprite.setPosition(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 1.5f);
                }
            }
        }
    }
}