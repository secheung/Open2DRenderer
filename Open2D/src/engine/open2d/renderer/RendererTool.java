package engine.open2d.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import engine.open2d.draw.Plane;
import engine.open2d.shader.Shader;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

public class RendererTool {
	private final static int BYTES_PER_FLOAT = 4;
	
	private float[] modelMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] projectionMatrix = new float[16];

	private int viewportWidth;
	private int viewportHeight;
	
	Map<String,Integer> handles;

	public RendererTool(){
		handles = new HashMap<String,Integer>();
	}

	public float[] getModelMatrix() {
		return modelMatrix;
	}

	public void setModelMatrix(float[] modelMatrix) {
		this.modelMatrix = modelMatrix;
	}

	public float[] getViewMatrix() {
		return viewMatrix;
	}

	public void setViewMatrix(float[] viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	public float[] getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(float[] projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public Map<String, Integer> getHandles() {
		return handles;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public void setViewportWidth(int viewportWidth) {
		this.viewportWidth = viewportWidth;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public void setViewportHeight(int viewportHeight) {
		this.viewportHeight = viewportHeight;
	}
	
	public void setHandles(Map<String, Integer> handles) {
		this.handles = handles;
	}
	
	public void setHandles(Shader shader){
		int shaderProgram = shader.getShaderProgram();

		//handles from shader
		for(String attribute:shader.getAttributes()){
			handles.put(attribute, GLES20.glGetAttribLocation(shaderProgram, attribute));
		}

		//handles for matrices
		handles.put("u_MVMatrix", GLES20.glGetUniformLocation(shaderProgram, "u_MVMatrix"));
		handles.put("u_MVPMatrix", GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix"));
		handles.put("u_Texture", GLES20.glGetUniformLocation(shaderProgram, "u_Texture"));
	}

	public void enableHandles(String attribute, float[] data, int dataElementSize){
		int handle = handles.get(attribute);
        FloatBuffer buffer = ByteBuffer.allocateDirect(data.length * BYTES_PER_FLOAT)
        								 .order(ByteOrder.nativeOrder())
        								 .asFloatBuffer();
        buffer.put(data).position(0);
        GLES20.glVertexAttribPointer(handle, dataElementSize, GLES20.GL_FLOAT, false, 0, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}
	
	public float[] screenUnProjection(float x, float y, float z){
		float[] modelView = getMVMatrix();
		float[] projectedPos = new float[4];
		int[] viewport = new int[4];
		
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
		float realY = viewportHeight - (int) y;
		
		float[] invertedMatrix = new float[16];
		float[] transformMatrix = new float[16];
		float[] normalizedInPoint = new float[4];
		
		normalizedInPoint[0] = (float) (x * 2.0f / viewportWidth - 1.0);
		normalizedInPoint[1] = (float) (realY * 2.0f / viewportHeight - 1.0);
		normalizedInPoint[2] = - 1.0f;
		normalizedInPoint[3] = 1.0f;
		
		Matrix.multiplyMM(	transformMatrix, 0,
							projectionMatrix, 0,
							modelView, 0);
		
		Matrix.invertM(	invertedMatrix, 0,
						transformMatrix, 0);
		
		Matrix.multiplyMV(	projectedPos, 0,
							invertedMatrix, 0,
							normalizedInPoint, 0);
		
		if (projectedPos[3] == 0.0){
			// Avoid /0 error.
			Log.e("World coords", "ERROR!");
			return null;
		}

		// Divide by the 3rd component to find
		// out the real position.
		
		projectedPos[0] = projectedPos[0] / projectedPos[3];
		projectedPos[1] = projectedPos[1] / projectedPos[3];
		projectedPos[2] = projectedPos[2] / projectedPos[3];

		
		Log.d("test", projectedPos[0] + " : " + projectedPos[1] + " : " + projectedPos[2]+ " : " + projectedPos[3]);
		
		return projectedPos;
	}
	
	public float[] screenProjection(Plane plane){
		int[] viewport = {0,0,viewportWidth,viewportHeight};
		
		float[] pos1 = new float[3];
		float[] pos2 = new float[3];
		float[] pos3 = new float[3];
		
		translateModelMatrix(plane.getTranslationX(), plane.getTranslationY(), plane.getTranslationZ());
		float[] posData = plane.getPositionData();
		
		float[] modelView = getMVMatrix();
		
		GLU.gluProject(		posData[0], posData[1], posData[2],
							modelView, 0,
							projectionMatrix, 0,
							viewport, 0,
							pos1, 0);
		
		GLU.gluProject(		posData[3], posData[4], posData[5],
							modelView, 0,
							projectionMatrix, 0,
							viewport, 0,
							pos2, 0);
		
		GLU.gluProject(		posData[6], posData[7], posData[8],
							modelView, 0,
							projectionMatrix, 0,
							viewport, 0,
							pos3, 0);

//		float width = pos3[0] - pos1[0];
//		float height = pos1[1] - pos2[1];
//		float depth = pos3[2];
//		float[] set = {pos1[0],pos1[1],width,height,depth};
		
		float width = pos1[0] - pos3[0];
		float height = pos1[1] - pos3[1];
		float depth = pos3[2];
		float[] set = {pos3[0],pos3[1],width,height,depth};
		
		return set;
		
	}
	
	public void setLookAt(int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
		Matrix.setLookAtM(viewMatrix, rmOffset, eyeX, eyeY, eyeZ, 
												centerX, centerY, centerZ,
												upX, upY, upZ);
	}

	public void setFrustum(int offset, float left, float right, float bottom, float top, float near, float far){
		Matrix.frustumM(projectionMatrix, offset, left, right, bottom, top, near, far);
	}

	public void translateModelMatrix(float changeX, float changeY, float changeZ){
		Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, changeX, changeY, changeZ);
	}
	
	//TODO fix this
	/*
	public void rotateModelMatrix(float angle, float rotationX, float rotationY, float rotationZ){
		//Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angle, rotationX, rotationY, rotationZ);
	}
	*/

	public float[] getMVMatrix(){
		float[] mvMatrix = new float[16];//TODO WILL THIS CAUSE PROBLEMS?????
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		return mvMatrix;
	}

	public float[] getMVPMatrix(){
		float[] mvMatrix = new float[16];
		float[] mvpMatrix = new float[16];
		//TODO TWO MATRIX MULT COULD CAUSE SLOWDOWN
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);

		return mvpMatrix;
	}
}