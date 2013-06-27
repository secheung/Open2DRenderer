package engine.open2d.renderer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView{

 	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private WorldRenderer worldRenderer;
    
    public SurfaceView(Context context){
        super(context);
        
        setEGLContextClientVersion(2);

        //WorldRenderer = WorldRenderer.getInstance();
        worldRenderer = new WorldRenderer(context);
        setRenderer(worldRenderer);
        
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
    	requestRender();
    	worldRenderer.passTouchEvents(e);
    
        return true;
    } 

}
