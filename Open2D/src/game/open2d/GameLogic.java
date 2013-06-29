package game.open2d;

import android.content.Context;
import engine.open2d.draw.Plane;
import engine.open2d.renderer.WorldRenderer;

public class GameLogic {
	WorldRenderer worldRenderer;
	Context context;

	public GameLogic(Context context, WorldRenderer worldRenderer){
		this.worldRenderer = worldRenderer;
		this.context = context;

		worldRenderer.addCustomShader(	WorldRenderer.WORLD_SHADER,
										R.raw.vertex_shader,
										R.raw.fragment_shader,
										new String[]{"a_Position","a_Color","a_Normal"}
									);

		/*
		float[] positionData = {
	            // X, Y, Z,
			3.0f,  3.0f, 0.0f,
			3.0f, -1.0f, 0.0f,
			7.0f,  3.0f, 0.0f,
			3.0f, -1.0f, 0.0f,
			7.0f, -1.0f, 0.0f,
			7.0f,  3.0f, 0.0f
		};
		*/

		float[] positionData = {
	            // X, Y, Z,
			 1.0f,  -1.0f, 0.0f,
			 1.0f,   1.0f, 0.0f,
			-1.0f,   1.0f, 0.0f,
			-1.0f,   1.0f, 0.0f,
			-1.0f,  -1.0f, 0.0f,
			 1.0f,  -1.0f, 0.0f
		};
		
		float[] colorData = {
		    // R, G, B, A
			1.0f, 0.0f, 0.0f, 1.0f,
			0.0f, 1.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 1.0f, 1.0f,
			0.0f, 0.0f, 0.0f, 1.0f,
			1.0f, 0.0f, 0.0f, 1.0f
		};

		float[] normalData = {
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f,
			0.0f, 0.0f, 1.0f
		};

		Plane plane = new Plane(positionData,colorData,normalData);
		worldRenderer.addDrawShape("myPlane", plane);
	}
}
