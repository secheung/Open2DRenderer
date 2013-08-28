package object;

import java.util.HashMap;
import java.util.LinkedHashMap;

import object.GameObject.Direction;
import android.util.Log;
import android.view.MotionEvent;
import engine.open2d.draw.Plane;
import engine.open2d.renderer.WorldRenderer;
import engine.open2d.texture.AnimatedTexture.Playback;
import game.open2d.GameLogic.LogicPlayState;
import game.open2d.GameTools;
import game.open2d.R;

public class Player extends GameObject{
	public static enum PlayerState implements GameObjectState{
		STAND("stand",0f,0f,0f),
		RUN("run",0f,0f,0f),
		DODGE("dodge",0f,0f,0f),
		DEAD("dead",0f,0f,0f),
		STRIKE1("strike1",-1.0f,0f,0f),
		STRIKE2("strike2",-1.0f,0f,0f),
		STRIKE3("strike3",-1.0f,0f,0f),
		FINISH("finish",0f,0f,0f);
		
		private static int STRIKE_NUMBERS = 3;
		
		public static PlayerState getRandomStrike(){
			double randNum = Math.random();
			int strike_number = (int) (randNum * STRIKE_NUMBERS + 1);
			
			return getStrike(strike_number);
		}
		
		public static PlayerState getStrike(int index){
			if(index < 1 || index > STRIKE_NUMBERS)
				return STRIKE2;
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("strike");
			buffer.append(index);
			for(PlayerState playerState : PlayerState.values()){
				if(buffer.toString().equals(playerState.getName())){
					return playerState;
				}
			}
			return null;
		}
		
		String name;
		float offSnapX;
		float offSnapY;
		float offSnapZ;
		PlayerState(String n, float x, float y, float z){
			name = n;
			offSnapX = x;
			offSnapY = y;
			offSnapZ = z;
		}
		
		public String getName(){
			return name;
		}
		
		public float getOffSnapX(){
			return offSnapX;
		}
		
		public float getOffSnapY(){
			return offSnapY;
		}
		
		public float getOffSnapZ(){
			return offSnapZ;
		}
	}
	
	public static String OBJNAME = "player";
	
	private static float WALK_SPEED = 0.2f;
	private static float STRIKE_SPEED = 0.1f;
	private static float BUFFER = 0.4f;
	private static float COLLISION_BUFFER = 1.0f;
	
	private Enemy struckEnemy;
	private PlayerState playerState;
	private float moveToX;
	private float moveToY;
	
	public Player(LinkedHashMap<String,GameObject> gameObjects, float x, float y, float width, float height){
		super(gameObjects,x,y,width,height);
		
		playerState = PlayerState.STAND;
		this.name = OBJNAME;
		
		this.moveToX = x;
		this.moveToY = y;
		this.z = -1.0f;
		
		animations = new HashMap<GameObjectState, Plane>();
		animations.put(PlayerState.STAND, new Plane(R.drawable.rising_stance, name+"_"+PlayerState.STAND.getName(), width, height, x, y, z, 4, 7));
		animations.put(PlayerState.RUN, new Plane(R.drawable.rising_run, name+"_"+PlayerState.RUN.getName(), width, height, x, y, z, 11, 3));
		animations.put(PlayerState.STRIKE1, new Plane(R.drawable.rising_strike1, name+"_"+PlayerState.STRIKE1.getName(), width, height, x, y, z, 2, 7));
		animations.put(PlayerState.STRIKE2, new Plane(R.drawable.rising_strike2, name+"_"+PlayerState.STRIKE2.getName(), width, height, x, y, z, 2, 7));
		animations.put(PlayerState.STRIKE3, new Plane(R.drawable.rising_strike3, name+"_"+PlayerState.STRIKE3.getName(), width, height, x, y, z, 2, 7));
		animations.put(PlayerState.FINISH, new Plane(R.drawable.finish1, name+"_"+PlayerState.FINISH.getName(), width, height, x, y, z, 8, 5));
		
		this.display = animations.get(PlayerState.STAND);
		this.direction = Direction.RIGHT;
	}

	public PlayerState getPlayerState() {
		return playerState;
	}

	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}

	@Override
	public void passTouchEvent(MotionEvent e, WorldRenderer worldRenderer){
		Plane selectedPlane = worldRenderer.getSelectedPlane(e.getX(), e.getY());
		float[] unprojectedPoints = worldRenderer.getUnprojectedPoints(e.getX(), e.getY(), display);
		
		if(playerState == PlayerState.RUN || playerState == PlayerState.STAND){
			moveToX = unprojectedPoints[0];
			moveToY = unprojectedPoints[1];
		} else if(	playerState == PlayerState.STRIKE1 ||
				playerState == PlayerState.STRIKE2 ||
				playerState == PlayerState.STRIKE3){
//			moveToX = x;
//			moveToY = y;
		}
		
		display.unprojectDisable();
//		if(animations.containsValue(selectedPlane)){
//			selected = true;
//		}
	}

	@Override
	public void updateState() {
		if(logicPlayState == logicPlayState.PLAY){
			
			if(playerState == PlayerState.RUN || playerState == PlayerState.STAND){
				executeMovement();
			}
			
			for(GameObject gameObject : gameObjects.values()){
				if(gameObject instanceof Enemy){
					executeEnemyInteraction((Enemy)gameObject);
				}
			}
		}
	}
	
	@Override
	public void updateLogic() {
		if(playerState == PlayerState.RUN){
			if(direction == Direction.RIGHT)
				x += WALK_SPEED;
			else if(direction == Direction.LEFT)
				x -= WALK_SPEED;
		}
		
		if(	playerState == PlayerState.STRIKE1 ||
			playerState == PlayerState.STRIKE2 ||
			playerState == PlayerState.STRIKE3){
			
			if(struckEnemy.getX() < x) {
				direction = Direction.LEFT;
				x = struckEnemy.getX()-playerState.getOffSnapX();
				moveToX = x+width/2;
			} else if(struckEnemy.getX() > x){
				direction = Direction.RIGHT;
				x = struckEnemy.getX()+playerState.getOffSnapX();
				moveToX = x+width/2;
			}
			
//			if(direction == Direction.RIGHT)
//				x += STRIKE_SPEED;
//			else if(direction == Direction.LEFT)
//				x -= STRIKE_SPEED;
		}
		
	}
	
	@Override
	public void updateDisplay() {
		if(direction==Direction.RIGHT){
			display.flipTexture(false);
		} else if(direction==Direction.LEFT){
			display.flipTexture(true);
		}
		
		switchAnimation(playerState);
	}
		
	@Override
	public void updateAfterDisplay() {
		if(display.isPlayed()){
			if(	playerState==PlayerState.FINISH ||
				playerState==PlayerState.STRIKE1||
				playerState==PlayerState.STRIKE2||
				playerState==PlayerState.STRIKE3){
				
				display.resetAnimation();
				playerState = PlayerState.STAND;
			}
		}
	}

	private void executeMovement(){
		float checkX = x+width/2;
		if(moveToX > checkX){
			direction = Direction.RIGHT;
			playerState = PlayerState.RUN;
		} else if(moveToX < checkX) {
			direction = Direction.LEFT;
			playerState = PlayerState.RUN;
		}

		if(moveToX > checkX - Player.BUFFER && moveToX < checkX + Player.BUFFER) {
			playerState = PlayerState.STAND;
		}
	}
	
	private void executeEnemyInteraction(Enemy enemy){
		if(GameTools.boxColDetect(this, enemy, COLLISION_BUFFER) && enemy.selected){
			playerState = PlayerState.getStrike((int)(PlayerState.STRIKE_NUMBERS*Math.random()));
			struckEnemy = enemy;
			
			if(enemy.getStruck() <= 0){
				playerState = PlayerState.FINISH;
			}
			
			if(x -  enemy.getX() < 0){
				direction = Direction.RIGHT;
			} else if(x -  enemy.getX() > 0) {
				direction = Direction.LEFT;
			}
		}
	}
}
