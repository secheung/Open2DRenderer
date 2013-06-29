package game.open2d;

import engine.open2d.renderer.WorldRenderer;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GameSurfaceView extends GLSurfaceView{

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private WorldRenderer worldRenderer;
    private GameLogic gameLogic;

    public GameSurfaceView(Context context){
		super(context);

		setEGLContextClientVersion(2);

		//WorldRenderer = WorldRenderer.getInstance();
		worldRenderer = new WorldRenderer(context);
		setRenderer(worldRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		gameLogic = new GameLogic(context,worldRenderer);
    }

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		requestRender();
		//worldRenderer.passTouchEvents(e);

		return true;
	}
}