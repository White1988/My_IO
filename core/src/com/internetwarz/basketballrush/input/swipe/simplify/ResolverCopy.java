package com.internetwarz.basketballrush.input.swipe.simplify;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.internetwarz.basketballrush.input.swipe.SwipeResolver;

public class ResolverCopy implements SwipeResolver {
	
	@Override
	public void resolve(Array<Vector2> input, Array<Vector2> output) {
		output.clear(); 
		output.addAll(input);
	}

}
