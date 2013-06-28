package engine.open2d.renderer;

import java.util.HashMap;
import java.util.Map;

import engine.open2d.shader.Shader;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class RendererMatrix { 
	public float[] modelMatrix = new float[16];
	public float[] viewMatrix = new float[16];
	public float[] projectionMatrix = new float[16];

	Map<String,Integer> handles;

	public RendererMatrix(){
		handles = new HashMap<String,Integer>();
	}

	public Map<String, Integer> getHandles() {
		return handles;
	}

	public void setHandles(Map<String, Integer> handles) {
		this.handles = handles;
	}

	public void setHandles(Shader shader){
		int shaderProgram = shader.getShaderProgram();

		//handles from shader
		for(String attribute:shader.getAttributes()){
			handles.put(attribute, GLES20.glGetUniformLocation(shaderProgram, attribute));
		}

		//handles for matrices
		handles.put("u_MVMatrix", GLES20.glGetUniformLocation(shaderProgram, "u_MVMatrix"));
		handles.put("u_MVPMatrix", GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix"));

		/*
		MVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix");
	    MVMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVMatrix");
	    LightPosHandle = GLES20.glGetAttribLocation(shaderProgram, "a_LightPos");
	    TextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Texture");
	    PositionHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position");
	    ColorHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Color");
	    NormalHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Normal");
	    TextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
	    */
	}

	public void setLookAt(int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ){
		Matrix.setLookAtM(viewMatrix, 0, 0.0f, 0.0f, -0.5f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
	}

	public void setFrustum(int offset, float left, float right, float bottom, float top, float near, float far){
		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}

	public void translateModelMatrix(float changeX, float changeY, float changeZ){
		Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, changeX, changeY, changeZ);
	}

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
		Matrix.multiplyMM(mvpMatrix, 0, mvMatrix, 0, projectionMatrix, 0);

		return mvpMatrix;
	}
}