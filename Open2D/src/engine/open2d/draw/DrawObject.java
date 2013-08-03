package engine.open2d.draw;

public abstract class DrawObject {
	protected float[] positionData;
	protected float[] colorData;
	protected float[] normalData;
	
	protected boolean draw;
	public String refName;
	
    protected float translationX;
    protected float translationY;
    protected float translationZ;

    protected float rotationX;
    protected float rotationY;
    protected float rotationZ;

    protected float scaleX;
    protected float scaleY;
    protected float scaleZ;
    
    protected abstract void drawObjectInit(float x, float y, float z);
    public abstract void update();
    
	public void setDraw(boolean enable){
		draw = enable;
	}
	
	public boolean isDrawEnabled(){
		return draw;
	}
    
	public float[] getPositionData() {
		return positionData;
	}

	public float[] getColorData() {
		return colorData;
	}

	public float[] getNormalData() {
		return normalData;
	}

	public float getTranslationX() {
		return translationX;
	}

	public void setTranslationX(float translationX) {
		this.translationX = translationX;
	}

	public float getTranslationY() {
		return translationY;
	}

	public void setTranslationY(float translationY) {
		this.translationY = translationY;
	}

	public float getTranslationZ() {
		return translationZ;
	}

	public void setTranslationZ(float translationZ) {
		this.translationZ = translationZ;
	}

	public float getRotationX() {
		return rotationX;
	}

	public void setRotationX(float rotationX) {
		this.rotationX = rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public void setRotationY(float rotationY) {
		this.rotationY = rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}

	public void setRotationZ(float rotationZ) {
		this.rotationZ = rotationZ;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getScaleZ() {
		return scaleZ;
	}

	public void setScaleZ(float scaleZ) {
		this.scaleZ = scaleZ;
	}

}