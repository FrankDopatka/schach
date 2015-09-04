import java.io.IOException;

import frontend.Frontend;


public class StartClient {
	static final String zumServer="http://10.0.2.15:8000";
	
	public static void main(String[] args) throws IOException{
		new Thread(){
			@Override
			public void run(){
				new Frontend(zumServer,true); // true: weiss
			}
		}.start();
		new Thread(){
			@Override
			public void run(){
				new Frontend(zumServer,false); // false: schwarz
			}
		}.start();
	}
}
