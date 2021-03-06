package structure;

import java.util.LinkedHashMap;

import android.graphics.PointF;

public class InteractionProperties extends ActionProperties {
	private int hitStop;
	private int hitStun;
	protected LinkedHashMap<String, PointF> triggerInitSpeeds;
	
	public double xInitDefaultSpeed = 0;
	public double yInitDefaultSpeed = 0;
	
	public InteractionProperties(){
		super();
		hitStop = 0;
		hitStun = 0;
		
		triggerInitSpeeds = new LinkedHashMap<String, PointF>();
	}
	
	public InteractionProperties(InteractionProperties other){
		copyActionProperties(other);
		
		this.hitStop = other.hitStop;
		this.hitStun = other.hitStun;
		
		this.triggerInitSpeeds = new LinkedHashMap<String, PointF>();
		this.triggerInitSpeeds.putAll(other.triggerInitSpeeds);
	}
	
	//TODO: Should really move away from this cause you forget to add stuff here
	public void copyActionProperties(ActionProperties properties){
		this.hitType = properties.hitType;
		this.xInitSpeed = properties.xInitSpeed;
		this.yInitSpeed = properties.yInitSpeed;
		this.xAccel = properties.xAccel;
		this.yAccel = properties.yAccel;
		
		this.triggerChange = new LinkedHashMap<String, String>();
		this.triggerChange.putAll(properties.triggerChange);
		
		this.triggerPropertiesList = new LinkedHashMap<String, TriggerProperties>();
		this.triggerPropertiesList.putAll(properties.triggerPropertiesList);
	}
	
	public int getHitStop() {
		return hitStop;
	}
	
	public void setHitStop(int hitStop) {
		this.hitStop = hitStop;
	}

	public int getHitStun() {
		return hitStun;
	}

	public void setHitStun(int hitStun) {
		this.hitStun = hitStun;
	}
	
	public void addTriggerInitSpeed(String trigger, PointF speed){
		triggerInitSpeeds.put(trigger, speed);
	}
	
	public PointF getTriggerInitSpeed(String trigger){
		return triggerInitSpeeds.get(trigger);
	}
	
	public boolean hasTriggerInitSpeed(String trigger){
		return triggerInitSpeeds.containsKey(trigger);
	}
}
