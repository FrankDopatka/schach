import java.io.IOException;

import frontend.Frontend;


public class StartClientWeiss {
	static final String zumServer="http://10.0.2.15:8000";
	
	public static void main(String[] args) throws IOException{
		new Frontend(zumServer,true); // true: weiss
	}
}
