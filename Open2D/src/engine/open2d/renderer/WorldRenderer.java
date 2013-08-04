package engine.open2d.renderer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import engine.open2d.draw.DrawObject;
import engine.open2d.draw.Plane;
import engine.open2d.shader.Shader;
import engine.open2d.shader.ShaderTool;
import engine.open2d.texture.Texture;
import engine.open2d.texture.TextureTool;

public class WorldRenderer implements GLSurfaceView.Renderer{
	private final static String LOG_PREFIX = "WORLD_RENDERER";
	private final static String ITEM_EXISTS_WARNING = "Item exists in world renderer.  No Item added.";
	private final static String NO_ITEM_EXISTS_WARNING = "No Item exists in ";
	
	public final static String WORLD_SHADER = "world_shader";

	Context activityContext;
	RendererTool rendererTool;
	
	ShaderTool shaderTool;
	TextureTool textureTool;

	LinkedHashMap<String,Shader> shaders;
	LinkedHashMap<String,DrawObject> drawObjects;

    public WorldRenderer(final Context activityContext) {
    	this.activityContext = activityContext;
    	rendererTool = new RendererTool();
    	shaderTool = new ShaderTool(activityContext);
    	textureTool = new TextureTool(activityContext);
    	
    	shaders = new LinkedHashMap<String,Shader>();
    	drawObjects = new LinkedHashMap<String,DrawObject>();
    }

	public void addDrawShape(String ref, DrawObject shape){
		if(drawObjects.containsKey(ref)){
			Log.w(LOG_PREFIX, ITEM_EXISTS_WARNING+" [shape : "+ref+"]");
			return;
		}

		shape.refName = ref;
    	drawObjects.put(ref, shape);
    }
	
    public void addCustomShader(String ref, int vertResourceId, int fragResourceId, String...attributes){
    	if(shaders.containsKey(ref)){
			Log.w(LOG_PREFIX, ITEM_EXISTS_WARNING+" [shader: "+ref+"]");
			return;
		}
    	
    	String vertShader = shaderTool.getShaderFromResource(vertResourceId);
    	String fragShader = shaderTool.getShaderFromResource(fragResourceId);
    	
    	Shader shader = new Shader(vertShader,fragShader,attributes);
    	
    	shaders.put(ref, shader);
    }

	public void initSetup(){

		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		rendererTool.setLookAt(	0,
								0.0f, 0.0f, 0.0f,
								0.0f, 0.0f, -1.0f,
								0.0f, 1.0f, 0.0f);

		buildShaders();
		buildObjectTextures();
	}

	private void buildShaders(){
	    if(shaders == null || shaders.isEmpty()){
			Log.w(LOG_PREFIX, NO_ITEM_EXISTS_WARNING +" shaders");
			return;
	    }

	    for(Shader shader : shaders.values())
	    	shaderTool.buildShaderProgram(shader);
	}

	private void buildObjectTextures(){
		if(drawObjects == null || drawObjects.isEmpty()){
			Log.w(LOG_PREFIX, NO_ITEM_EXISTS_WARNING+ " textures");
			return;
	    }
		
	    for(DrawObject shape : drawObjects.values()){
    		textureTool.loadTexture(((Plane)shape).getTexture());
	    }
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		initSetup();
	}
	
	public void drawObject(DrawObject drawObject){
		drawObject.update();
	}
	
	public void drawObject(DrawObject drawObj, float x, float y, float z){
		drawObj.setTranslationX(x);
		drawObj.setTranslationY(y);
		drawObj.setTranslationZ(z);
		
		drawObj.update();
	}

	public void passTouchEvents(MotionEvent e){
	} 
	
	public Plane getSelectedObjection(float xCoord ,float yCoord){
		float x = xCoord;
		float y = rendererTool.getViewportHeight() - yCoord;
		float closestdepth = -1;
		Plane objSelected=null;

		for(DrawObject drawObj : drawObjects.values()){
			if(!drawObj.isDrawEnabled()){
				continue;
			}

			float[] projectedPoints = rendererTool.screenProjectPlane((Plane)drawObj);

			if(x > projectedPoints[0] && x < projectedPoints[0]+projectedPoints[2] && y > projectedPoints[1] && y < projectedPoints[1]+projectedPoints[3]){
				if(projectedPoints[4] >= closestdepth){
					closestdepth = projectedPoints[4];
					objSelected = (Plane)drawObj;
				}
			}
		}
		
		return objSelected;
	}
	
	public float[] getUnprojectedPoints(float x, float y, String drawObj){
		Plane drawObject = (Plane)drawObjects.get(drawObj);
		return rendererTool.screenUnProjection(x,y,drawObject.getTranslationZ());
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		int worldShaderProgram = shaders.get(WORLD_SHADER).getShaderProgram();

		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(worldShaderProgram);

		rendererTool.setHandles(shaders.get(WORLD_SHADER));

		//TODO order planes according to z value
		for(DrawObject drawObject : drawObjects.values()){
			if(drawObject.isDrawEnabled()){
				drawShape(drawObject);
			}
		}
	}

	private void drawShape(DrawObject drawObject){
		float[] positionData = drawObject.getPositionData();
		float[] colorData = drawObject.getColorData();
		float[] normalData = drawObject.getNormalData();

		//TODO Textures
		//TODO MAKE SO NOT HARDCODED
		Map<String,Integer> handles = rendererTool.getHandles();

		if(drawObject instanceof Plane){
			rendererTool.enableHandles("a_Position", positionData, Plane.POSITION_DATA_SIZE);
			rendererTool.enableHandles("a_Color", colorData, Plane.COLOR_DATA_SIZE);
			rendererTool.enableHandles("a_Normal", normalData, Plane.NORMAL_DATA_SIZE);
			
			Plane plane = (Plane)drawObject;
		    if(!(plane.getTexture() == null)){
	
		    	int textureUniformHandle = handles.get("u_Texture");
	
			    //TODO needs object index on active and uniform
			    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, plane.getTexture().getCompiledTexture());
		    	GLES20.glUniform1i(textureUniformHandle,0);
		    	
		    	Texture shapeTexture = plane.getTexture();
		    	float[] textureData = shapeTexture.getTextureCoord();
		    	rendererTool.enableHandles("a_TexCoordinate", textureData, Plane.TEXTURE_DATA_SIZE);
		    }
	    }

	    int mvMatrixHandle = handles.get("u_MVMatrix");
	    int mvpMatrixHandle = handles.get("u_MVPMatrix");
        
	    rendererTool.translateModelMatrix(drawObject.getTranslationX(),drawObject.getTranslationY(),drawObject.getTranslationZ());
        
		GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, rendererTool.getMVMatrix(), 0);
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, rendererTool.getMVPMatrix(), 0);

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

	}
	
	public void setCamera(float x, float y, float z){
		rendererTool.setLookAt(	0,
								x, y, z,
								x, y, z-1.0f,
								0.0f, 1.0f, 0.0f);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		rendererTool.setViewportWidth(width);
		rendererTool.setViewportHeight(height);
		
		final float ratio = (float) width/height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 10.0f;

		rendererTool.setFrustum(0, left, right, bottom, top, near, far);
	}
}