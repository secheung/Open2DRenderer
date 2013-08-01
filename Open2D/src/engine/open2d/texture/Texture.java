package engine.open2d.texture;

public class Texture {
	protected int resourceId;
	protected int compiledTexture;
	protected float[] textureCoord = {	1.0f, 0.0f,
										0.0f, 0.0f,
										0.0f, 1.0f,
										0.0f, 1.0f,
										1.0f, 1.0f,
										1.0f, 0.0f	};
	
	public Texture(int resourceId){
		this.resourceId = resourceId;
	}

	public float[] getTextureCoord() {
		return textureCoord;
	}
	
	public void setTextureCoord(float[] textureCoord){
		this.textureCoord = textureCoord;  
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public int getCompiledTexture() {
		return compiledTexture;
	}

	public void setCompiledTexture(int compiledTexture) {
		this.compiledTexture = compiledTexture;
	}
}