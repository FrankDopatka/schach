import java.io.IOException;

import frontend.Frontend;

public class StartClientSchwarz {
	public static void main(String[] args) throws IOException{
		new Frontend(Konstanten.zumServer,false); // false: schwarz
	}
}
